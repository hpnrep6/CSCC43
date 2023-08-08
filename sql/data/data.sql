INSERT INTO User
VALUES 
("u1", "Joe", "312 Road Drive", "1998-02-05", "Farmer", 142000542), 
("u2", "Null", "53 Union Avenue", "1990-12-03", "Fisher", 242000642), 
("u3", "Maple", "22 Military Trail", "1974-02-03", "Technician", 342000542), 
("u4", "Potato", "34 Ellesmere Rd", "1952-08-01", "Retired", 442230542), 
("u5", "John", "45 Don Valley Rd", "1978-05-08", "Customer Service", 542550542), 
("u6", "Bob", "3 Street Blvd", "1968-02-03", "Food Preparation", 641200542);

INSERT INTO Renter
VALUES 
("u1", "3233 2315 8924 8126"),
("u2", "2222 3333 4523 5555");

INSERT INTO Listing
VALUES 
(1, "Apartment", 50, 50, "10 House Rd", "M1C3P4", "Toronto", "Canada", 8, 0, "u3"),
(2, "Apartment", 51, 49, "12 House Rd", "M1C3P3", "Toronto", "Canada", 28, 0, "u3"),
(3, "Apartment", 52, 54, "14 House Rd", "M1C3G9", "Toronto", "Canada", 18, 0, "u3"),
(4, "House", 80, 40, "16 House Rd", "M1G3P4", "Toronto", "Canada", 3, 0, "u4"),
(5, "Guesthouse", 70, 20, "19 House Rd", "M1K3P4", "Toronto", "Canada", 9, 0, "u5"),
(6, "Apartment", 50, 50, "10 House Rd", "G1F3P4", "Havana", "Cuba", 8, 0, "u6"),
(7, "Apartment", 100, 23, "10 House Rd", "L8K2CB", "Salta", "Argentina", 82, 0, "u6"),
(8, "Apartment", 87, 53, "10 House Rd", "G4CK8O", "Kingston", "Canada", 6, 0, "u6");

INSERT INTO Availability
VALUES
(1, "2023-01-01", "2023-05-01", 432),
(1, "2023-05-03", "2023-07-12", 436),
(1, "2023-09-01", "2023-11-05", 32),
(2, "2023-01-01", "2023-10-01", 321),
(3, "2023-01-01", "2023-02-01", 22),
(3, "2023-05-01", "2023-8-01", 456),
(4, "2023-09-01", "2023-11-01", 435),
(5, "2023-01-01", "2023-11-09", 45),
(6, "2023-01-01", "2023-10-22", 23),
(7, "2023-01-01", "2023-11-12", 99),
(8, "2023-01-01", "2023-11-18", 792);

INSERT INTO Ratings 
VALUES 
(1, "u2", "I like this place"),
(2, "u2", "This place is strange"),
(3, "u2", "What is this place?"),
(4, "u2", "So many trees"),
(5, "u2", "There are deer and raccoons here"),
(6, "u2", "Good area"),
(7, "u2", "Not sure about this person"),
(8, "u2", "Good person I guess?"),
(9, "u4", "Hello")


INSERT INTO ListingRating
VALUES
(1, 1, 4),
(1, 2, 3),
(3, 3, 2),
(4, 4, 1),
(5, 5, 2),
(1, 6, 5)

INSERT INTO UserRating
VALUES
("u2", 7),
("u1", 8),
("u1", 9)

INSERT INTO Booking
VALUES 
(1, "u1", 324, "2023-02-03", "2023-02-06", 0, "2023-01-01", NULL),
(1, "u4", 424, "2023-02-07", "2023-02-09", 0, "2023-01-01", NULL),
(2, "u2", 124, "2023-07-03", "2023-08-06", 0, "2023-01-01", NULL),
(3, "u2", 35, "2023-05-03", "2023-05-06", 0, "2023-01-01", NULL),
(3, "u1", 329, "2023-06-13", "2023-06-26", 0, "2023-01-01", NULL),
(4, "u2", 221, "2023-10-03", "2023-10-16", 0, "2023-01-01", NULL),
(7, "u1", 54, "2023-02-03", "2023-03-06", 0, "2023-01-01", NULL),
(6, "u2", 324, "2023-02-03", "2023-02-06", 0, "2023-01-01", NULL)