package mybnb;

import com.fasterxml.jackson.databind.ObjectMapper;
import mybnb.utils.MyBnBUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@SpringBootApplication
@RestController
public class MybnbApplication {
	static Connection connection = null;
	public static void main(String[] args) {
		SpringApplication.run(MybnbApplication.class, args);

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/MyBnB?user=root&password=");
			System.out.println("SQL Connected");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@RequestMapping(value = "/")
	public ModelAndView home() {
		ModelAndView view = new ModelAndView();
		view.setViewName("index.html");
		return view;
	}

	@PostMapping("/api/user/create")
	public String createUser(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement statement = connection.createStatement();
			statement.executeUpdate(
					String.format("INSERT INTO User VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %s)",
							bodyMap.get("username"),
							bodyMap.get("name"),
							bodyMap.get("address"),
							bodyMap.get("date-of-birth"),
							bodyMap.get("occupation"),
							bodyMap.get("sin")
							)
			);
		} catch (Exception e) {
			return "{\"status\": \"" + e.toString().substring(e.toString().indexOf(":") + 1) + "\"}";
		}
		return "{\"status\": \"Success\"}";
	}

	@PostMapping("/api/user/validate")
	public String validateUser(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery("SELECT * FROM User WHERE Id LIKE \""+ bodyMap.get("username")+"\"");
			if (r.next()) {
				return MyBnBUtils.generateStatus("Success");
			}
		} catch (Exception e) {

		}
		return MyBnBUtils.generateStatus("User not found");
	}

	@GetMapping("/api/listingtypes/get")
	public String getListingTypes() {
		try {
			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery("SELECT Name FROM ResidenceType");

			ArrayList<String> list = new ArrayList<>();

			while (r.next()) {
				list.add(r.getString(1));
			}

			String ret = "{ \"types\": [";
			for (int i = 0; i < list.size(); ++i) {
				ret += "\"" + list.get(i) + "\"";

				if (i < list.size() - 1)
				ret += ",";
			}
			ret += "] }";

			return ret;
		} catch (Exception e) {
			return e.toString();
		}
	}

	private static String str(Object s) {
		return "\"" + s.toString() + "\"";
	}

	@PostMapping("/api/listing/create")
	public String createListing(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);
			Statement statement = connection.createStatement();

			String time = Long.toString(new Date().getTime());
			System.out.println(body);
			System.out.println(2323);
//			List<BnBDate> availabilityMap = new ObjectMapper().readValue(bodyMap.get("availability"), List.class);

			String datesString = "" + bodyMap.get("availability");
			String[] datesSplit = datesString.substring(1, datesString.length() - 1).replace("}, {", "}.{").split("\\.");
//			System.out.println(bodyMap.get("availability"));
			System.out.println(datesSplit.length);

			List<Map<String, Objects>> dateList = new ArrayList<>();

			for (int i = 0; i < datesSplit.length; ++i) {
				System.out.println(datesSplit[i]);
				String modDate = datesSplit[i].replace("{", "{\"").replace("}", "\"}").replace("=", "\":\"").replace(", ", "\", \"");
				System.out.println(modDate);

				Map<String, Objects> dateMap = new ObjectMapper().readValue(modDate, HashMap.class);
				dateList.add(dateMap);
			}
			System.out.println(12313);
			statement.executeUpdate(
					String.format(
							"INSERT INTO Listing VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
							time,
							str(bodyMap.get("type")),
							bodyMap.get("lat"),
							bodyMap.get("long"),
							str(bodyMap.get("address")),
							str(bodyMap.get("postal-code")),
							str(bodyMap.get("city")),
							str(bodyMap.get("country")),
							str(bodyMap.get("max-guests")),
							"0",
							str(bodyMap.get("user"))
							)
			);

			for (int i = 0; i < dateList.size(); ++i) {
				statement = connection.createStatement();
				Map<String, Objects> cur = dateList.get(i);

				statement.executeUpdate(
						String.format(
								"INSERT INTO Availability VALUES (%s, %s, %s, %s)",
								time, str(cur.get("start")), str(cur.get("end")), cur.get("price")
						)
				);
			}

			return MyBnBUtils.generateStatus("Success");
		} catch (Exception e) {
			System.out.println(e.toString());
			return MyBnBUtils.generateStatus(e.toString());
		}
	}

	@PostMapping("/api/search/coords")
	public String searchListingCoords(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			double lon = Double.parseDouble("" + bodyMap.get("long"));
			double lat = Double.parseDouble("" + bodyMap.get("lat"));
			double dist = Double.parseDouble("" + bodyMap.get("distance"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Listing " +
									"WHERE Latitude >= %s AND Latitude <= %s" +
									" AND Longitude >= %s AND Longitude <= %s"
							, "" + (lat - dist), "" + (lat + dist), "" + (lon - dist), "" + (lon + dist)
					)
			);

			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", r.getString(1));
				obj.put("type", r.getString(2));
				obj.put("latitude", r.getString(3));
				obj.put("longitude", r.getString(4));
				obj.put("address", r.getString(5));
				obj.put("postal_code", r.getString(6));
				obj.put("city", r.getString(7));
				obj.put("country", r.getString(8));
				obj.put("max_guests", r.getString(9));
				results.add(obj);
			}
			return results.toString();
		} catch (Exception e) {
			System.out.println(e);
			return MyBnBUtils.generateStatus("Error");
		}
	}

	@PostMapping("/api/search/postal")
	public String searchListingPostal(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String code = ("" + bodyMap.get("postal")).substring(0, 4);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Listing " +
									"WHERE Postal_Code LIKE \"%s__\"",
							code
					)
			);

			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", r.getString(1));
				obj.put("type", r.getString(2));
				obj.put("latitude", r.getString(3));
				obj.put("longitude", r.getString(4));
				obj.put("address", r.getString(5));
				obj.put("postal_code", r.getString(6));
				obj.put("city", r.getString(7));
				obj.put("country", r.getString(8));
				obj.put("max_guests", r.getString(9));
				results.add(obj);
			}
			return results.toString();
		} catch (Exception e) {
			System.out.println(e);
			return MyBnBUtils.generateStatus("Error");
		}
	}

	@PostMapping("/api/search/address")
	public String searchListingAddress(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String code = ("" + bodyMap.get("address"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Listing " +
									"WHERE Address LIKE \"%s\"",
							code
					)
			);

			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", r.getString(1));
				obj.put("type", r.getString(2));
				obj.put("latitude", r.getString(3));
				obj.put("longitude", r.getString(4));
				obj.put("address", r.getString(5));
				obj.put("postal_code", r.getString(6));
				obj.put("city", r.getString(7));
				obj.put("country", r.getString(8));
				obj.put("max_guests", r.getString(9));
				results.add(obj);
			}
			return results.toString();
		} catch (Exception e) {
			System.out.println(e);
			return MyBnBUtils.generateStatus("Error");
		}
	}

	@PostMapping("/api/listing/get")
	public String getListing(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String code = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Listing " +
									"WHERE Id = %s",
							code
					)
			);

			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", r.getString(1));
				obj.put("type", r.getString(2));
				obj.put("latitude", r.getString(3));
				obj.put("longitude", r.getString(4));
				obj.put("address", r.getString(5));
				obj.put("postal_code", r.getString(6));
				obj.put("city", r.getString(7));
				obj.put("country", r.getString(8));
				obj.put("max_guests", r.getString(9));
				results.add(obj);
			}
			return results.toString();
		} catch (Exception e) {
			System.out.println(e);
			return MyBnBUtils.generateStatus("Error");
		}
	}

	@PostMapping("/api/listing/pricing")
	public String checkListingPrice(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String id = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Availability " +
									"WHERE ListingId = %s ORDER BY StartDate",
							id
					)
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("start", r.getString(2));
				object.put("end", r.getString(3));
				object.put("price", r.getString(4));
				results.add(object);
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/listing/check")
	public String checkListingDate(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String start = ("" + bodyMap.get("start"));
			String end = ("" + bodyMap.get("end"));
			String id = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Availability " +
									"WHERE ListingId = %s AND " +
									"StartDate <= \"%s\" AND EndDate >= \"%s\""
							,id, start, end)
			);

			if (!r.next()) {
				return MyBnBUtils.generateStatus("Date not available");
			}

			s = connection.createStatement();
			r = s.executeQuery(
					String.format(
							"SELECT * FROM Booking " +
									"WHERE ListingId = %s AND " +
									"(StartDate <= \"%s\" AND EndDate >= \"%s\") OR " +
									"(StartDate <= \"%s\" AND EndDate >= \"%s\")"
							,id, start, start, end, end)
			);

			if (r.next()) {
				return MyBnBUtils.generateStatus("Date has already been booked, choose another date.");
			}

			return MyBnBUtils.generateStatus("Available");
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/booking/create")
	public String createBooking(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String start = ("" + bodyMap.get("start"));
			String end = ("" + bodyMap.get("end"));
			String id = ("" + bodyMap.get("id"));
			String user = ("" + bodyMap.get("user"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Availability " +
									"WHERE ListingId = %s AND " +
									"StartDate <= \"%s\" AND EndDate >= \"%s\""
							,id, start, end)
			);
			System.out.println(user);
			if (!r.next()) {
				return MyBnBUtils.generateStatus("Date not available");
			}

			long price = r.getLong(4);

			s = connection.createStatement();
			s.executeUpdate(
					String.format(
							"INSERT INTO Booking VALUES (%s, \"%s\", %s, \"%s\", \"%s\", %s, \"%s\", NULL)"
							, id, user, price, start, end, "" + 0, "2023-08-08")
			);

			return MyBnBUtils.generateStatus("Successfully booked");
		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/booking/get")
	public String getBookings(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String user = ("" + bodyMap.get("user"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT ListingId, Type, Address, StartDate, EndDate FROM Booking " +
									" JOIN Listing ON Booking.ListingId = Listing.Id " +
									"WHERE Booking.UserId LIKE \"%s\" AND Booking.Cancelled = 0"
							, user)
			);

			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", r.getString(1));
				obj.put("type", r.getString(2));
				obj.put("address", r.getString(3));
				obj.put("start", r.getString(4));
				obj.put("end", r.getString(5));
				results.add(obj);
			}
			return results.toString();

		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/booking/cancel")
	public String cancelBooking(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String id = ("" + bodyMap.get("id"));
			String start = ("" + bodyMap.get("start"));


			Statement s = connection.createStatement();
			 s.executeUpdate(
					String.format(
							"UPDATE Booking SET Cancelled = 1 " +
									"WHERE ListingId = %s AND StartDate = \"%s\""
							, id, start)
			);
			System.out.println("Deleted");
			return MyBnBUtils.generateStatus("Success");

		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/listings/get")
	public String getListingsUser(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String user = ("" + bodyMap.get("user"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Listing " +
									"WHERE OwnerId LIKE \"%s\""
							, user)
			);

			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject obj = new JSONObject();
				obj.put("id", r.getString(1));
				obj.put("type", r.getString(2));
				obj.put("latitude", r.getString(3));
				obj.put("longitude", r.getString(4));
				obj.put("address", r.getString(5));
				obj.put("postal_code", r.getString(6));
				obj.put("city", r.getString(7));
				obj.put("country", r.getString(8));
				obj.put("max_guests", r.getString(9));
				results.add(obj);
			}
			return results.toString();

		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/listings/details")
	public String getListingDetails(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String id = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Listing " +
									"WHERE Id = %s"
							, id)
			);


			r.next();
			JSONObject obj = new JSONObject();
			obj.put("id", r.getString(1));
			obj.put("type", r.getString(2));
			obj.put("latitude", r.getString(3));
			obj.put("longitude", r.getString(4));
			obj.put("address", r.getString(5));
			obj.put("postal_code", r.getString(6));
			obj.put("city", r.getString(7));
			obj.put("country", r.getString(8));
			obj.put("max_guests", r.getString(9));

			s = connection.createStatement();
			r = s.executeQuery(
					String.format(
							"SELECT * FROM Availability " +
									"WHERE ListingId = %s"
							, id)
			);

			JSONArray availability = new JSONArray();

			while (r.next()) {
				JSONObject o = new JSONObject();
				o.put("start", r.getString(2));
				o.put("end", r.getString(3));
				o.put("price", r.getLong(4));
				availability.add(o);
			}

			obj.put("availability", availability);

			return obj.toString();

		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/listings/update")
	public String createUpdateListing(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);
			Statement statement = connection.createStatement();


			String datesString = "" + bodyMap.get("availability");
			String[] datesSplit = datesString.substring(1, datesString.length() - 1).replace("}, {", "}.{").split("\\.");
//			System.out.println(bodyMap.get("availability"));
			System.out.println(datesSplit.length);

			List<Map<String, Objects>> dateList = new ArrayList<>();

			for (int i = 0; i < datesSplit.length; ++i) {
				System.out.println(datesSplit[i]);
				String modDate = datesSplit[i].replace("{", "{\"").replace("}", "\"}").replace("=", "\":\"").replace(", ", "\", \"");
				System.out.println(modDate);

				Map<String, Objects> dateMap = new ObjectMapper().readValue(modDate, HashMap.class);
				dateList.add(dateMap);
			}

			statement.executeUpdate(
					String.format(
							"DELETE FROM Availability WHERE ListingId = %s",
							"" + bodyMap.get("id")
					)
			);

			for (int i = 0; i < dateList.size(); ++i) {
				statement = connection.createStatement();
				Map<String, Objects> cur = dateList.get(i);

				statement.executeUpdate(
						String.format(
								"INSERT INTO Availability VALUES (%s, %s, %s, %s)",
								"" + bodyMap.get("id"), str(cur.get("start")), str(cur.get("end")), cur.get("price")
						)
				);
			}

			return MyBnBUtils.generateStatus("Success");
		} catch (Exception e) {
			System.out.println(e.toString());
			return MyBnBUtils.generateStatus(e.toString());
		}
	}

	@PostMapping("/api/listings/delete")
	public String deleteListing(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);
			Statement statement = connection.createStatement();

			statement.executeUpdate(
					String.format(
							"DELETE FROM Listing WHERE Id = %s",
							"" + bodyMap.get("id")
					)
			);

			return MyBnBUtils.generateStatus("Success");
		} catch (Exception e) {
			System.out.println(e.toString());
			return MyBnBUtils.generateStatus(e.toString());
		}
	}

	@PostMapping("/api/comment/get")
	public String getCommentsLIsting(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String id = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT AuthorId, Content, Stars FROM Ratings " +
									" INNER JOIN ListingRating ON ListingRating.CommentId = Ratings.Id " +
									" WHERE ListingRating.ListingId = %s ORDER BY Stars",
							id
					)
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("author", r.getString(1));
				object.put("content", r.getString(2));
				object.put("stars", r.getString(3));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/listingcomment/create")
	public String createListingComment(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String stars = ("" + bodyMap.get("stars"));
			String comment = ("" + bodyMap.get("comment"));
			String id = ("" + bodyMap.get("id"));
			String user = ("" + bodyMap.get("user"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Booking " +
									"WHERE ListingId = %s AND " +
									"UserId = \"%s\" AND EndDate <= \"2023-08-08\""
							,id, user)
			);

			if (!r.next()) {
				return MyBnBUtils.generateStatus("You have not rented here before");
			}

			String time = Long.toString(new Date().getTime());

			s = connection.createStatement();
			s.executeUpdate(
					String.format(
							"INSERT INTO Ratings VALUES (%s, \"%s\", \"%s\")"
							, time, user, comment)
			);

			s = connection.createStatement();
			s.executeUpdate(
					String.format(
							"INSERT INTO ListingRating VALUES (%s, \"%s\", \"%s\")"
							, id, time, stars)
			);

			return MyBnBUtils.generateStatus("Success");
		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/listings/bookings")
	public String getListingBookings(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String id = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT UserId, Price, StartDate, EndDate FROM Booking " +
									"WHERE ListingId = %s AND Cancelled = 0"
							, id)
			);


			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("user", r.getString(1));
				object.put("price", r.getString(2));
				object.put("start", r.getString(3));
				object.put("end", r.getString(3));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();

		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/user/delete")
	public String deleteAccount(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String user = ("" + bodyMap.get("user"));


			Statement s = connection.createStatement();
			s.executeUpdate(
					String.format(
							"DELETE FROM User WHERE Id = \"%s\"",
							user
					)
			);
			System.out.println("Deleted");
			return MyBnBUtils.generateStatus("Success");

		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/rating/get")
	public String getRatingsUser(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String id = ("" + bodyMap.get("id"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT AuthorId, Content FROM Ratings " +
									" INNER JOIN UserRating ON UserRating.CommentId = Ratings.Id " +
									" WHERE UserRating.UserId = \"%s\"",
							id
					)
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("author", r.getString(1));
				object.put("content", r.getString(2));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/userrating/create")
	public String creatUserRating(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			String comment = ("" + bodyMap.get("comment"));
			String id = ("" + bodyMap.get("id"));
			String user = ("" + bodyMap.get("user"));

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					String.format(
							"SELECT * FROM Booking " +
									" JOIN Listing ON Booking.ListingId = Listing.Id " +
									" WHERE Listing.OwnerId LIKE \"%s\" AND " +
									" UserId LIKE \"%s\" AND EndDate <= \"2023-08-08\""
							,user, id)
			);

			if (!r.next()) {
				return MyBnBUtils.generateStatus("This user has not rented from you before");
			}

			String time = Long.toString(new Date().getTime());

			s = connection.createStatement();
			s.executeUpdate(
					String.format(
							"INSERT INTO Ratings VALUES (%s, \"%s\", \"%s\")"
							, time, user, comment)
			);

			s = connection.createStatement();
			s.executeUpdate(
					String.format(
							"INSERT INTO UserRating VALUES (\"%s\", %s)"
							, id, time)
			);

			return MyBnBUtils.generateStatus("Success");
		} catch (Exception e) {
			System.out.println(e);
		}

		return MyBnBUtils.generateStatus("Failed");
	}

	@PostMapping("/api/report/city")
	public String getCityTotal(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
							"SELECT City, COUNT(DISTINCT ListingId, StartDate) " +
									" FROM Booking JOIN Listing ON Listing.Id = Booking.ListingId " +
									" GROUP BY City"
					);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("city", r.getString(1));
				object.put("count", r.getString(2));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/report/postal")
	public String getPostalTotal(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					"SELECT Postal_Code, COUNT(DISTINCT ListingId, StartDate) " +
							" FROM Booking JOIN Listing ON Listing.Id = Booking.ListingId " +
							" GROUP BY Postal_Code"
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("city", r.getString(1));
				object.put("count", r.getString(2));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/report/country")
	public String getCOuntryTotal(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					"SELECT Country, COUNT(DISTINCT ListingId, StartDate) " +
							" FROM Booking JOIN Listing ON Listing.Id = Booking.ListingId " +
							" GROUP BY Country"
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("city", r.getString(1));
				object.put("count", r.getString(2));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/report/countrycity")
	public String getCOuntryTotalC(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					"SELECT Country, City, COUNT(DISTINCT ListingId, StartDate) " +
							" FROM Booking JOIN Listing ON Listing.Id = Booking.ListingId " +
							" GROUP BY Country, City"
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("city", r.getString(1) + ", " + r.getString(2));
				object.put("count", r.getString(3));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}

	@PostMapping("/api/report/countrycitypostal")
	public String getCOuntryTotalCC(@RequestBody String body) {
		try {
			Map<String, Objects> bodyMap = new ObjectMapper().readValue(body, HashMap.class);

			Statement s = connection.createStatement();
			ResultSet r = s.executeQuery(
					"SELECT Country, City, Postal_Code, COUNT(DISTINCT ListingId, StartDate) " +
							" FROM Booking JOIN Listing ON Listing.Id = Booking.ListingId " +
							" GROUP BY Country, City, Postal_Code"
			);
			JSONArray results = new JSONArray();

			while (r.next()) {
				JSONObject object = new JSONObject();
				object.put("city", r.getString(1) + ", " + r.getString(2) + ", " + r.getString(3));
				object.put("count", r.getString(4));
				results.add(object);

				System.out.println(r.getString(1));
			}
			return results.toString();
		} catch (Exception e) {

		}

		return "";
	}
}
