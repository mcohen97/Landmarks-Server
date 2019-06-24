IF OBJECT_ID('dbo.LandmarkTour', 'U') IS NOT NULL 
  DROP TABLE dbo.LandmarkTour;
IF OBJECT_ID('dbo.Tour', 'U') IS NOT NULL 
  DROP TABLE dbo.Tour;
IF OBJECT_ID('dbo.LandmarkImages', 'U') IS NOT NULL 
  DROP TABLE dbo.LandmarkImages;
IF OBJECT_ID('dbo.LandmarkAudios', 'U') IS NOT NULL 
  DROP TABLE dbo.LandmarkAudios;
IF OBJECT_ID('dbo.Landmark', 'U') IS NOT NULL 
  DROP TABLE dbo.Landmark;

CREATE TABLE Landmark(
ID INT CHECK(ID>=0) PRIMARY KEY,
TITLE VARCHAR(100) NOT NULL,
LATITUDE FLOAT NOT NULL,
LONGITUDE FLOAT NOT NULL,
DESCRIPTION VARCHAR(2500) NOT NULL
);

CREATE TABLE LandmarkImages(
LANDMARK_ID INT FOREIGN KEY REFERENCES Landmark,
ID INT NOT NULL,
EXTENSION VARCHAR(5) NOT NULL CHECK(EXTENSION IN ('jpg', 'jpeg', 'gif','png'))
PRIMARY KEY(LANDMARK_ID,ID)
);

CREATE TABLE LandmarkAudios(
LANDMARK_ID INT FOREIGN KEY REFERENCES Landmark,
ID INT NOT NULL CHECK(ID>=0),
PRIMARY KEY(LANDMARK_ID,ID)
);

CREATE TABLE Tour(
ID INT NOT NULL CHECK(ID>=0) PRIMARY KEY,
TITLE VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(1000) NOT NULL,
CATEGORY VARCHAR(20) NOT NULL CHECK(CATEGORY in ('GREEN_SITES','ENTERTAINMENT','CULTURAL', 'SIGHT_SEEING')),
IMAGE_EXTENSION VARCHAR(20) NOT NULL CHECK(IMAGE_EXTENSION IN ('jpg', 'jpeg', 'gif','png'))
);

CREATE TABLE LandmarkTour(
TOUR_ID INT FOREIGN KEY REFERENCES Tour,
LANDMARK_ID INT FOREIGN KEY REFERENCES Landmark,
STOP_NUMBER INT NOT NULL CHECK(STOP_NUMBER>=0),
PRIMARY KEY(LANDMARK_ID,TOUR_ID),
UNIQUE(TOUR_ID,STOP_NUMBER)
);
