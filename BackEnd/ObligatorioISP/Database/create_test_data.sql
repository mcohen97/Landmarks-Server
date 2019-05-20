INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (1,'Monumento 1',-34.912126,-56.167282,'Descripcion 1');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (2,'Monumento 2',-34.912900,-56.162263,'Descripcion 2');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (3,'Monumento 3',-34.914202,-56.157930,'Descripcion 3');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (4,'Monumento 4',-34.900618,-56.12474,'Descripcion 4');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (5,'Monumento 5',-34.908192,-56.150494,'Descripcion 5');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (6,'Monumento 6',-34.885015,-56.159562,'Descripcion 6');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (7,'Monumento 7',-34.885138,-56.157508,'Descripcion 7');
INSERT INTO Landmark(ID,TITLE, LATITUDE, LONGITUDE,DESCRIPTION) VALUES (8,'Monumento 8',-34.885156,-56.159798,'Descripcion 8');

INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(1,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(2,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(3,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(4,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(5,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(6,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(7,1,'jpg');
INSERT INTO LandmarkImages(LANDMARK_ID,ID,EXTENSION) VALUES(8,1,'jpg');

INSERT INTO LandmarkAudios(LANDMARK_ID,ID) VALUES(1,1);


INSERT INTO Tour(ID,TITLE,DESCRIPTION,CATEGORY,IMAGE_EXTENSION) VALUES(1, 'Tour 1','description 1','CULTURAL','jpg');
INSERT INTO Tour(ID,TITLE,DESCRIPTION,CATEGORY,IMAGE_EXTENSION) VALUES(2, 'Tour 2','description 2','ENTERTAINMENT','jpg');


INSERT INTO LandmarkTour(TOUR_ID, LANDMARK_ID, STOP_NUMBER) VALUES (1,1,1);
INSERT INTO LandmarkTour(TOUR_ID, LANDMARK_ID, STOP_NUMBER) VALUES (1,2,2);
INSERT INTO LandmarkTour(TOUR_ID, LANDMARK_ID, STOP_NUMBER) VALUES (1,3,3);

INSERT INTO LandmarkTour(TOUR_ID, LANDMARK_ID, STOP_NUMBER) VALUES (2,6,1);
INSERT INTO LandmarkTour(TOUR_ID, LANDMARK_ID, STOP_NUMBER) VALUES (2,7,2);
INSERT INTO LandmarkTour(TOUR_ID, LANDMARK_ID, STOP_NUMBER) VALUES (2,8,3);