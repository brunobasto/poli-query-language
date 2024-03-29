package com.application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.ql.parser.Parser;
import com.ql.parser.exception.SyntaxException;

public class Main {

	private static ArrayList<String> getFirstNameFirst()
		throws IOException {

		ArrayList<String> names = new ArrayList<String>();

		ClassLoader classLoader = Main.class.getClassLoader();

		InputStream in = classLoader.getResourceAsStream(
			"META-INF/data/random-names.csv");

		if (in == null) {
			throw new FileNotFoundException(
				"The database CSV file was not found.");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;

		while ((line = br.readLine()) != null) {
			String[] nameFormats = line.split(",");

			names.add(nameFormats[3]);
		}

		in.close();

		return names;
	}

	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));

		try {
			ArrayList<String> database = getFirstNameFirst();

			String query;

			System.out.println(
				"Type a query search in the prompt below or just press enter to exit.");

			do {
				System.out.print("Search: ");

				query = reader.readLine();

				if (query.isEmpty()) {
					break;
				}

				try {
					long timeStart = System.currentTimeMillis();

					Parser parser = new Parser(query);

					ArrayList<String> results = parser.filter(database);

					long timeEnd = System.currentTimeMillis();

					for (String name : results) {
						System.out.println(name);
					}

					System.out.println(
						"Found " + results.size() + " results in " + (timeEnd - timeStart) + "ms");
				}
				catch (SyntaxException se) {
					System.out.println(
						"An unexpected error occured: " + se.getMessage());

					System.out.println(
						"-- Remember that strings MUST come between double quotes (E.g.: \"Edward\")");

					break;
				}
			}
			while (!query.isEmpty());
		} catch (IOException ioe) {
			System.out.println(
				"An unexpected error occured: " + ioe.getMessage());
		}
	}

}
