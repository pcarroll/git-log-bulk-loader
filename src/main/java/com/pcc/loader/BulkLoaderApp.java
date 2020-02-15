package com.pcc.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pcc.loader.dao.AuthorRepository;
import com.pcc.loader.dao.FileCommitRepository;
import com.pcc.loader.dao.ProjectFileRepository;
import com.pcc.loader.pojo.Author;
import com.pcc.loader.pojo.FileChange;
import com.pcc.loader.pojo.FileCommit;
import com.pcc.loader.pojo.ProjectFile;

/**
 * Load!
 *
 */
@SpringBootApplication
public class BulkLoaderApp implements CommandLineRunner {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private FileCommitRepository fileCommitRepository;

	@Autowired
	private ProjectFileRepository projectFileRepository;

	//  State machine states
	private static final int START_STATE = 0;
	private static final int MERGE_OR_AUTHOR_STATE = 1;
	private static final int IGNORE_UNTIL_COMMIT_STATE = 2;
	private static final int DATE_STATE = 3;
	private static final int COMMENT_STATE = 4;
	private static final int FILE_STATE = 5;
	
	//  Transition strings
	private static final String COMMIT_STRING = "commit";
	private static final String MERGE_STRING = "Merge:";
	private static final String AUTHOR_STRING = "Author:";
	private static final String DATE_STRING = "Date:";
	private static final String COMMENT_STRING = "    ";

	//  Various bits of state
	private String hash = null;
	private Author author = null;
	private String date = null;
	private List<String> commentList = new ArrayList<>();
	
	private Set<Author> authors = new HashSet<>();
	private Set<ProjectFile> projectFiles = new HashSet<>();
	
	private List<FileChange> fileChangeList = new ArrayList<>();

	//  Run the loader
	public static void main(String[] args) {
		SpringApplication.run(BulkLoaderApp.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			//  Many strings are passed to the run method, the file we're parsing is last.
			File file = new File(args[args.length - 1]);

			//  Set up the reader for the log file
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			//  Initialize the state of the machine
			int state = START_STATE;

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				switch (state) {
				case START_STATE:
					if (line.startsWith(COMMIT_STRING)) {
						//  Simplification of a non-deterministic FSM
						state = MERGE_OR_AUTHOR_STATE;
						hash = extractHash(line);
					}
					break;
				case MERGE_OR_AUTHOR_STATE:
					if (line.startsWith(MERGE_STRING)) {
						state = IGNORE_UNTIL_COMMIT_STATE;
					}
					if (line.startsWith(AUTHOR_STRING)) {
						state = DATE_STATE;
						author = extractAuthor(line);
					}
					break;
				case DATE_STATE:
					if (line.startsWith(DATE_STRING)) {
						state = COMMENT_STATE;
						date = extractDate(line);
					}
					break;
				case COMMENT_STATE:
					if (line.startsWith(COMMENT_STRING)) {
						state = COMMENT_STATE;
						commentList.add(extractComment(line));
					} else {
						state = FILE_STATE;
						fileChangeList.add(extractFile(line));
					}
					break;
				case FILE_STATE:
					if (line.startsWith(COMMIT_STRING)) {
						// Add code here to act on the previous commit.
						store(hash, author, date, commentList, fileChangeList);
						reset();
						//  And around we go again
						state = MERGE_OR_AUTHOR_STATE;
						hash = extractHash(line);
					} else {
						fileChangeList.add(extractFile(line));
					}
					break;
				case IGNORE_UNTIL_COMMIT_STATE:
					if (line.startsWith(COMMIT_STRING)) {
						state = MERGE_OR_AUTHOR_STATE;
						hash = extractHash(line);
					} 
					break;
				}
			}

			//  Assume we hit EOF with accumulated state
			store(hash, author, date, commentList, fileChangeList);

			//  Store the authors and project files
			wrapUp();

			//  close the reader
			bufferedReader.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	private String extractHash(String line) {
		String hash = line.substring(6).trim();
		return hash;
	}

	private Author extractAuthor(String line) {
		String name = line.substring(7, line.indexOf('<') - 1).trim();
		String email = line.substring(line.indexOf('<') + 1, line.length() - 1).trim();

		Author author = new Author(name, email);

		authors.add(author);
		return author;
	}

	private String extractDate(String line) {
		String date = line.substring(5).trim();
		return date;
	}

	private String extractComment(String line) {
		String comment = line.trim();
		return comment;
	}

	private FileChange extractFile(String line) {
		String[] lineElements = line.split("\\s+");

		//  Some files (e.g., Excel spreadsheets) need special handling
		if ("-".equals(lineElements[0])) {
			lineElements[0] = "0";  //  Lines added
		}

		if ("-".equals(lineElements[1])) {
			lineElements[1] = "0";  //  Lines deleted
		}

		projectFiles.add(new ProjectFile(lineElements[2]));  //  File name

		FileChange fileChange = 
				new FileChange(lineElements[2], 
						Integer.valueOf(lineElements[0]).intValue(),
						Integer.valueOf(lineElements[1]).intValue());
		return fileChange;
	}

	private void store(String hash, Author author, String date, List<String> commentList,
			List<FileChange> fileChangeList) {

		//  Comments may stretch over many lines, depending on length.
		StringBuilder commentBuilder = new StringBuilder();
		for (String comment : commentList) {
			commentBuilder.append(comment).append(' ');
		}
		String comment = commentBuilder.toString().trim();

		for (FileChange fileChange : fileChangeList) {
			FileCommit fileCommit = 
					new FileCommit(hash, 
							author.getEmail(), 
							comment, 
							fileChange.getFileName(),
							fileChange.getLinesAdded(), 
							fileChange.getLinesDeleted());

			fileCommitRepository.save(fileCommit);
		}
	}

	//  Reset the bits of state used on each loop through the machine.
	private void reset() {
		hash = null;
		author = null;
		date = null;
		commentList.clear();
		fileChangeList.clear();
	}

	//  Store all the authors and project files.
	private void wrapUp() {
		for (Author author : authors) {
			authorRepository.save(author);
		}

		for (ProjectFile projectFile : projectFiles) {
			projectFileRepository.save(projectFile);
		}
	}
}
