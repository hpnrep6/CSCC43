CREATE DATABASE MyBnB;

USE MyBnB;

CREATE TABLE User (
    Id VARCHAR(128) NOT NULL,
    Name VARCHAR(128) NOT NULL,
    Address VARCHAR(256) NOT NULL,
    DateOfBirth DATE NOT NULL,
    Occupation VARCHAR(256) NOT NULL,
    SIN DEC(9, 0) NOT NULL,
    PRIMARY KEY (Id)
);

CREATE TABLE Renter (
    UserId VARCHAR(128) NOT NULL,
    CardNumber VARCHAR(64) NOT NULL,
    PRIMARY KEY (UserId),
    FOREIGN KEY (UserId) REFERENCES User(Id) ON DELETE CASCADE
);

CREATE TABLE Country (
    Name VARCHAR(164) NOT NULL,
    PRIMARY KEY (Name)
);

CREATE TABLE ResidenceType (
    Name VARCHAR(128) NOT NULL,
    PRIMARY KEY (Name)
);

CREATE TABLE Listing (
    Id BIGINT NOT NULL AUTO_INCREMENT,
    Type VARCHAR(128) NOT NULL,
    Latitude DEC (9, 6) NOT NULL,
    Longitude DEC (9, 6) NOT NULL,
    Address TINYTEXT NOT NULL,
    Postal_Code TINYTEXT NOT NULL,
    City TINYTEXT NOT NULL,
    Country VARCHAR(164) NOT NULL,
    Max_Guests SMALLINT NOT NULL,
    Published BOOL NOT NULL DEFAULT 0,
    OwnerId VARCHAR(128) NOT NULL,
    PRIMARY KEY (Id),
    FOREIGN KEY (OwnerId) REFERENCES User(Id) ON DELETE CASCADE,
    FOREIGN KEY (Country) REFERENCES Country(Name),
    FOREIGN KEY (Type) REFERENCES ResidenceType(Name)
);

-- Country ENUM ("Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Côte d'Ivoire", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Holy See", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau", "Palestine State", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States of America", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe") NOT NULL,

CREATE TABLE Amenities_Choice (
    Name VARCHAR(128) NOT NULL,
    PRIMARY KEY (Name)
);

CREATE TABLE Amenity (
    ListingId BIGINT NOT NULL,
    Name VARCHAR(128) NOT NULL,
    PRIMARY KEY (ListingId),
    FOREIGN KEY (ListingId) REFERENCES Listing(Id) ON DELETE CASCADE,
    FOREIGN KEY (Name) REFERENCES Amenities_Choice(Name) ON DELETE CASCADE
);

CREATE TABLE Availability (
    ListingId BIGINT NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Price BIGINT NOT NULL,
    PRIMARY KEY (ListingId, StartDate),
    FOREIGN KEY (ListingId) REFERENCES Listing(Id) ON DELETE CASCADE
);

CREATE TABLE Ratings (
    Id BIGINT NOT NULL AUTO_INCREMENT,
    AuthorId VARCHAR(128) NOT NULL,
    Content MEDIUMTEXT NOT NULL,
    PRIMARY KEY (Id),
    FOREIGN KEY (AuthorId) REFERENCES User(Id) ON DELETE CASCADE
);

CREATE TABLE ListingRating (
    ListingId BIGINT NOT NULL,
    CommentId BIGINT NOT NULL,
    Stars TINYINT,
    PRIMARY KEY (CommentId),
    FOREIGN KEY (ListingId) REFERENCES Listing(Id) ON DELETE CASCADE,
    FOREIGN KEY (CommentId) REFERENCES Ratings(Id) ON DELETE CASCADE
);

CREATE TABLE UserRating (
    UserId VARCHAR(128) NOT NULL,
    CommentId BIGINT NOT NULL,
    PRIMARY KEY (CommentId),
    FOREIGN KEY (UserId) REFERENCES User(Id) ON DELETE CASCADE,
    FOREIGN KEY (CommentId) REFERENCES Ratings(Id) ON DELETE CASCADE
);

CREATE TABLE Booking (
    ListingId BIGINT NOT NULL,
    UserId VARCHAR(128) NOT NULL,
    Price BIGINT NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Cancelled BOOL NOT NULL DEFAULT 0,
    BookingDate DATE NOT NULL,
    CancellationDate DATE,
    FOREIGN KEY (ListingId) REFERENCES Listing(Id) ON DELETE CASCADE,
    FOREIGN KEY (UserId) REFERENCES User(Id)
);