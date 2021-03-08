package basic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class FoodSafety {
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String[] fileNames = { "knowsley", "liverpool", "sefton", "sthelens", "wigan", "wirral" };

		List<Business> businesses = readFiles(fileNames);

		String str = "a";

		do {
			System.out.println(" ------------------------------------------------- ");
			System.out.println("Choose one from the following or press enter to quit: ");
			System.out.println("A. List all distinct (unique) Local Authorities ");
			System.out.println("B. List all distinct (unique) businesses (BusinessName) for which you have data.");
			System.out.println(
					"C. List all premises ratings (Ratings and Scores), for a specified business, in chronological order of rating date (most recent first).");
			System.out.println("D. List output details, where a rating is:");
			System.out.println("F. Find the least or most satisfactory rating for a given organisation.");
			System.out.println(
					"G. In a given local authority, find the organisation with the most rating 5 or 0/1 scores");
			System.out.println(
					"H. In a specified authority, count total of each Business Type and output details, sorted from highest to lowest.");
			System.out.println("Type A or B or C or D or F or G or H");
			str = scanner.nextLine();
			switch (str) {
			case "A":
                listUniqueAuthoritiesFrom(businesses);
                break;
			case "B":
				listUniqueBusinessNamesFrom(businesses);
				break;
			case "C":
				listPremisesRatings(businesses);
				break;
			case "D":
				listOutputDetails(businesses);
				break;
			case "F":
				findSatisfactoryRating(businesses);
				break;
			case "G":
				findOrganisationWithMostScores(businesses);
				break;
			case "H":
				countAndListOccurances(businesses);
				break;
			default:
				System.out.println("Invalid Input");
				break;
			}
		} while (!str.isEmpty());

		scanner.close();

	}

	private static void findOrganisationWithMostScores(List<Business> businesses) {
		System.out.println("Specify authority:");
		String authorityName = scanner.nextLine();
		String businessName = " ";

		System.out.println(" ------------------------------------------------- ");
		System.out.println("Find the organisation with the: ");
		System.out.println("1. Most rating 5 scores");
		System.out.println("2. Most rating 0/1 scores");
		System.out.println("Type 1 or 2");
		String str = scanner.nextLine();
        
		switch (str) {
		case "1":
			businessName = findOrgMostFiveScores(businesses, authorityName);
			break;
		case "2":
			businessName = findOrgMostOneZeroScores(businesses, authorityName);
			break;
		default:
			System.out.println("Invalid Input");
		}
		
		ArrayList<Business> mostRatedBusiness = new ArrayList<Business>();

		// get all business record

		for (Business business : businesses) {
			if (business.localAuthorityName.equalsIgnoreCase(authorityName)
					&& business.businessName.equalsIgnoreCase(businessName)) {
				mostRatedBusiness.add(business);
			}
		}

		// sort

		for (int i = 0; i < mostRatedBusiness.size(); i++) {
			for (int j = i; j > 0; j--) {
				Business current = mostRatedBusiness.get(j);
				Business previous = mostRatedBusiness.get(j - 1);

				if (current.ratingDate.compareTo(previous.ratingDate) > 0) {
					mostRatedBusiness.set(j - 1, current);
					mostRatedBusiness.set(j, previous);
				}
			}
		}
		
		System.out.println("Business Name: " + businessName + " | Authority Name: " + authorityName);
		for (Business business : mostRatedBusiness) {
			System.out.println("Date: " + business.ratingDate + " | Score Hygiene: " + business.scoresHygiene
					+ " | Score Structural: " + business.scoresStructural + " | Score Confidence: "
					+ business.scoresConfidenceInManagement);
		}
		
	}

	private static String findOrgMostFiveScores(List<Business> businesses, String authorityName) {
		ArrayList<String> businessNames = new ArrayList<String>();
		ArrayList<Integer> totalScores = new ArrayList<Integer>();

		for (Business business : businesses) {
			if (business.localAuthorityName.equalsIgnoreCase(authorityName)) {
				int scores = 0;
				if (business.scoresHygiene.equals("5")) {
					scores++;
				} else if (business.scoresStructural.equals("5")) {
					scores++;
				} else if (business.scoresConfidenceInManagement.equals("5")) {
					scores++;
				}
				int position = businessNames.indexOf(business.businessName);
				if (position >= 0) {
					int previous = totalScores.get(position);
					totalScores.set(position, previous + scores);
				} else {
					businessNames.add(business.businessName);
					totalScores.add(scores);
				}
			}
		}

		String businessName = "";
		int maxScore = 0;

		for (int i = 0; i < businessNames.size(); i++) {
			if (totalScores.get(i) > maxScore) {
				businessName = businessNames.get(i);
				maxScore = totalScores.get(i);
			}
		}

		return businessName;
	}

	private static String findOrgMostOneZeroScores(List<Business> businesses, String authorityName) {
		ArrayList<String> businessNames = new ArrayList<String>();
		ArrayList<Integer> totalScores = new ArrayList<Integer>();

		for (Business business : businesses) {
			if (business.localAuthorityName.equalsIgnoreCase(authorityName)) {
				int scores = 0;
				if (business.scoresHygiene.equals("0") || business.scoresStructural.equals("1")) {
					scores++;
				} else if (business.scoresStructural.equals("0") || business.scoresStructural.equals("1")) {
					scores++;
				} else if (business.scoresConfidenceInManagement.equals("0")
						|| business.scoresConfidenceInManagement.equals("1")) {
					scores++;
				}
				int position = businessNames.indexOf(business.businessName);
				if (position >= 0) {
					int previous = totalScores.get(position);
					totalScores.set(position, previous + scores);
				} else {
					businessNames.add(business.businessName);
					totalScores.add(scores);
				}
			}
		}

		String businessName = "";
		int maxScore = 0;

		for (int i = 0; i < businessNames.size(); i++) {
			if (totalScores.get(i) > maxScore) {
				businessName = businessNames.get(i);
				maxScore = totalScores.get(i);
			}
		}

		return businessName;
	}

	private static void listOutputDetails(List<Business> businesses) {
		List<Business> ratedBusinesses = new ArrayList<Business>();

		System.out.println(" ------------------------------------------------- ");
		System.out.println("List output details, where a rating is: ");
		System.out.println("1. Above a specified value");
		System.out.println("2. Below a specified value");
		System.out.println("3. Within a specified range (eg. '1 - 3')");
		System.out.println("4. A special value");
		System.out.println("Type 1 or 2 or 3 or 4");
		String str = scanner.nextLine();
		switch (str) {
		case "1":
			ratedBusinesses = aboveSpecifiedRating(businesses);
			break;
		case "2":
			ratedBusinesses = belowSpecifiedRating(businesses);
			break;
		case "3":
			ratedBusinesses = withinSpecifiedRating(businesses);
			break;
		case "4":
			ratedBusinesses = specialSpecifiedRating(businesses);
			break;
		default:
			System.out.println("Invalid Input");
		}
		
		System.out.println("--- Feature E ---");
		System.out.println(
				"Enter name of local authority for which you want details or leave blank for an overall total");
		System.out.println("Enter authority name:");
		String authorityName = scanner.nextLine();

		System.out.println("Enter business type for which you want rating details or leave blank for an overall total");
		System.out.println("Enter business type: ");
		String businessType = scanner.nextLine();

		for (Business business : ratedBusinesses) {
			if (authorityName.isBlank() || business.localAuthorityName.equalsIgnoreCase(authorityName)) {
				if (businessType.isBlank() || business.businessType.equalsIgnoreCase(businessType)) {
					System.out.println("Authority: " + business.localAuthorityName + " | Type: " + business.businessType
							+ " | Name: " + business.businessName + " | Rating: " + business.ratingValue);
				}
			}
		}

	}

	private static List<Business> aboveSpecifiedRating(List<Business> businesses) {
		List<Business> ratedBusinesses = new ArrayList<Business>();
		System.out.println("Specify integer value:");
		String value = scanner.nextLine();

		for (Business business : businesses) {
			try {
				if (Integer.parseInt(business.ratingValue) > Integer.parseInt(value)) {
					ratedBusinesses.add(business);
				}
			} catch (NumberFormatException e) {
			}
		}

		return ratedBusinesses;
	}

	private static List<Business> belowSpecifiedRating(List<Business> businesses) {
		List<Business> ratedBusinesses = new ArrayList<Business>();
		System.out.println("Specify integer value:");
		String value = scanner.nextLine();

		for (Business business : businesses) {
			try {
				if (Integer.parseInt(business.ratingValue) < Integer.parseInt(value)) {
					ratedBusinesses.add(business);
				}
			} catch (NumberFormatException e) {
			}
		}
		return ratedBusinesses;
	}

	private static List<Business> withinSpecifiedRating(List<Business> businesses) {
		List<Business> ratedBusinesses = new ArrayList<Business>();
		System.out.println("Specify minimum integer value:");
		String value = scanner.nextLine();
		System.out.println("Specify maximum integer value:");
		String value2 = scanner.nextLine();

		for (Business business : businesses) {
			try {
				if (Integer.parseInt(business.ratingValue) >= Integer.parseInt(value)
						&& Integer.parseInt(business.ratingValue) <= Integer.parseInt(value2)) {
					ratedBusinesses.add(business);
				}
			} catch (NumberFormatException e) {
			}
		}
		return ratedBusinesses;
	}

	private static List<Business> specialSpecifiedRating(List<Business> businesses) {
		List<Business> ratedBusinesses = new ArrayList<Business>();
		System.out.println("Specify special value:");
		String value = scanner.nextLine();

		for (Business business : businesses) {
			if (business.ratingValue.equalsIgnoreCase(value)) {
				ratedBusinesses.add(business);
			}

		}
		return ratedBusinesses;
	}

	private static void listPremisesRatings(List<Business> businesses) {
		System.out.println("Enter business name: ");
		String businessName = scanner.nextLine();

		List<Business> userSpecifiedBusinesses = new ArrayList<Business>();

		for (Business business : businesses) {
			if (business.businessName.equalsIgnoreCase(businessName)) {
				userSpecifiedBusinesses.add(business);
			}
		}

		// sort
		for (int i = 0; i < userSpecifiedBusinesses.size(); i++) {
			for (int j = i; j > 0; j--) {
				Business current = userSpecifiedBusinesses.get(j);
				Business previous = userSpecifiedBusinesses.get(j - 1);

				if (current.ratingDate.compareTo(previous.ratingDate) > 0) {
					userSpecifiedBusinesses.set(j - 1, current);
					userSpecifiedBusinesses.set(j, previous);
				}
			}
		}

		for (Business business : userSpecifiedBusinesses) {
			System.out.println("Date: " + business.ratingDate + " | Rating: " + business.ratingValue + " | Hygiene: "
					+ business.scoresHygiene + " | Structural: " + business.scoresStructural
					+ " | Confidence In Management: " + business.scoresConfidenceInManagement);
		}

	}

	// de-serialize data
	static List<Business> readFiles(String[] fileNames) throws FileNotFoundException, IOException {
		List<Business> businesses = new ArrayList<Business>();

		for (int i = 0; i < fileNames.length; i++) {
			Reader in = new FileReader("src\\data\\" + fileNames[i] + ".csv");
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);

			for (CSVRecord record : records) {
				Business business = new Business();
				business.fhrsid = record.get("FHRSID");
				business.localAuthorityBusinessID = record.get("LocalAuthorityBusinessID");
				business.businessName = record.get("BusinessName");
				business.businessType = record.get("BusinessType");
				business.businessTypeID = record.get("BusinessTypeID");
				business.addressLine1 = record.get("AddressLine1");
				business.addressLine2 = record.get("AddressLine2");
				business.addressLine3 = record.get("AddressLine3");
				business.addressLine4 = record.get("AddressLine4");
				business.postCode = record.get("PostCode");
				business.ratingValue = record.get("RatingValue");
				business.ratingKey = record.get("RatingKey");
				business.ratingDate = record.get("RatingDate");
				business.localAuthorityCode = record.get("LocalAuthorityCode");
				business.localAuthorityName = record.get("LocalAuthorityName");
				business.localAuthorityWebSite = record.get("LocalAuthorityWebSite");
				business.localAuthorityEmailAddress = record.get("LocalAuthorityEmailAddress");
				business.scoresHygiene = record.get("Scores/Hygiene");
				business.scoresStructural = record.get("Scores/Structural");
				business.scoresConfidenceInManagement = record.get("Scores/ConfidenceInManagement");
				business.schemeType = record.get("SchemeType");
				business.newRatingPending = record.get("NewRatingPending");
				business.geocodeLongitude = record.get("Geocode/Longitude");
				business.geocodeLatitude = record.get("Geocode/Latitude");

				// RightToReply is not present in all files so if it's not present, just ignore
				try {
					business.rightToReply = record.get("RightToReply");
				} catch (Exception IllegalArgumentException) {
				}

				businesses.add(business);
			}
		}
		return businesses;
	}

	static void listUniqueAuthoritiesFrom(List<Business> array) {
		List<Business> unique = new ArrayList<Business>();

		for (Business row : array) {
			if (!isLocalAuthorityCodeInArray(unique, row.localAuthorityCode)) {
				unique.add(row);
				System.out.println(row.localAuthorityCode + ", " + row.localAuthorityName + ", "
						+ row.localAuthorityEmailAddress + ", " + row.localAuthorityWebSite);
			}
		}
	}

	// checks if localAuthorityCode is already in the given array and if so returns
	// true, false otherwise
	static boolean isLocalAuthorityCodeInArray(List<Business> array, String localAuthorityCode) {
		for (Business business : array) {
			if (business.localAuthorityCode.equalsIgnoreCase(localAuthorityCode)) {
				return true;
			}
		}
		return false;
	}

	static void listUniqueBusinessNamesFrom(List<Business> array) {
		ArrayList<String> businessNames = new ArrayList<String>();

		for (Business row : array) {
			if (!isBusinessNameInArray(businessNames, row.businessName)) {
				businessNames.add(row.businessName);
				System.out.println(row.businessName);
			}
		}
		System.out.println("Total Businesses: " + array.size());
		System.out.println("Total Unique Businesses: " + businessNames.size());

	}

	static boolean isBusinessNameInArray(List<String> businessNames, String businessName) {
		for (String name : businessNames) {
			if (name.equals(businessName)) {
				return true;
			}
		}
		return false;
	}

	static void countAndListOccurances(List<Business> array) {
		ArrayList<String> businessTypes = new ArrayList<String>();
		ArrayList<Integer> occurrences = new ArrayList<Integer>();

		// ask for authority name

		System.out.println("Enter authority name or leave it blank for an overall total: ");
		String authorityName = scanner.nextLine();

		// count occurrences
		for (Business business : array) {

			// if authority name matches, then continue with the code
			if (authorityName.isBlank() || authorityName.equalsIgnoreCase(business.localAuthorityName)) {
				int position = businessTypes.indexOf(business.businessType);
				if (position >= 0) {
					int previous = occurrences.get(position);
					occurrences.set(position, previous + 1);
				} else {
					businessTypes.add(business.businessType);
					occurrences.add(1);
				}
			}
		}
		
		// sorting algorithm
		for (int i = 0; i < occurrences.size(); i++) {
			for (int j = i; j > 0; j--) {
				int current = occurrences.get(j);
				if (occurrences.get(j - 1) < current) {
					occurrences.set(j, occurrences.get(j - 1));
					occurrences.set(j - 1, current);
					String previous = businessTypes.get(j - 1);
					businessTypes.set(j - 1, businessTypes.get(j));
					businessTypes.set(j, previous);
				}
			}
		}

		// if there are no details, output no matching authority
		if (businessTypes.size() == 0) {
			System.out.println("No matching authority name");
		} else {
			// else output all details
			if (!authorityName.isBlank()) {
				System.out.println("List of overall total for each Business Type in " + authorityName);
			} else {
				System.out.println("List of overall total for each Business Type");
			}
			for (int i = 0; i < businessTypes.size(); i++) {
				System.out.println("BusinessType: " + businessTypes.get(i) + " | Occurrences: " + occurrences.get(i));
			}
		}
		
		
	}

	static void findSatisfactoryRating(List<Business> array) {
		String organisationName = null;
		String answer = null;
		int rating = -1;
		Business organisation = null;

		do {
			System.out.println("Enter organisation name: ");
			organisationName = scanner.nextLine();
		} while (organisationName.isBlank());

		do {
			System.out.println("Do you want to find most or least satisfactory rating? Write 'least' or 'most'.");
			answer = scanner.nextLine().toLowerCase();
			if (answer.equals("least")) {
				rating = 6;
				break;
			} else if (answer.equals("most")) {
				break;
			}
		} while (true);

		for (Business business : array) {
			if (business.businessName.equalsIgnoreCase(organisationName)) {
				try {
					int businessRating = Integer.parseInt(business.ratingValue);
					if (answer.equals("most") && businessRating > rating
							|| answer.equals("least") && businessRating < rating) {
						rating = businessRating;
						organisation = business;
					}
				} catch (Exception NumberFormatException) {
					if (rating == -1 || rating == 6) {
						organisation = business;
					}
				}
			}
		}

		if (organisation != null) {
			System.out.println(organisation.businessName + " has a " + answer + " satisfactory rating of "
					+ organisation.ratingValue);
		} else {
			System.out.println("Didn't find an organisation.");
		}
	}
}
