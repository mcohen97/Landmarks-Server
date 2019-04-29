using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.BusinessLogic;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;

namespace ObligatorioISP.DataAccess.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class LandmarksRepositoryTest
    {
        private SqlServerLandmarksRepository landmarks;
        private TestDatabaseManager testData;


        [TestInitialize]
        public void StartUp()
        {
            testData = new TestDatabaseManager();
            testData.SetUpDatabase();
            testData.LoadTestData();
            landmarks = new SqlServerLandmarksRepository(testData.ConnectionString,testData.ImagesPath,testData.AudiosPath);
        }

        [TestMethod]
        public void ShouldGiveLandmarksWithinBounds()
        {
            double centerLat = -34.923844;
            double centerLng = -56.170590;

            ICollection<Landmark> withinBounds = landmarks.GetWithinZone(centerLat, centerLng, 2);
            Assert.AreEqual(3, withinBounds.Count);
        }

        [TestMethod]
        public void ShouldGiveLandmarksCoveredByTour() {
            int tourId = 1;
            ICollection<Landmark> fromTour = landmarks.GetTourLandmarks(tourId);
            Assert.AreEqual(3, fromTour.Count);
        }

        [TestMethod]
        public void ShouldReturnLandmarkWithTheId() {
            int landmarkId = 2;
            Landmark retrieved = landmarks.GetById(2);
            Assert.AreEqual(retrieved.Id, 2);
            Assert.AreEqual(retrieved.Title, "Monumento 2");
        }

        [TestMethod]
        [ExpectedException(typeof(LandmarkNotFoundException))]
        public void ShouldThrowExceptionIfLandmarkNotFound() {
            Landmark retrieved = landmarks.GetById(32);
        }
    }
}