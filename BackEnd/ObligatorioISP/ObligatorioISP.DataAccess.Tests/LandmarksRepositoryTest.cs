using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using Moq;

namespace ObligatorioISP.DataAccess.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class LandmarksRepositoryTest
    {
        private SqlServerLandmarksRepository landmarks;
        private TestDatabaseManager testData;
        private ISqlContext context;

        [TestInitialize]
        public void StartUp()
        {
            testData = new TestDatabaseManager();
            testData.SetUpDatabase();
            testData.LoadTestData();
            context = new SqlServerConnectionManager(testData.ConnectionString);
            landmarks = new SqlServerLandmarksRepository(context,testData.LandmarksImagesPath,testData.AudiosPath);
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
            Assert.AreEqual(2, retrieved.Id);
            Assert.AreEqual("Monumento 2",retrieved.Title);
        }

        [TestMethod]
        [ExpectedException(typeof(LandmarkNotFoundException))]
        public void ShouldThrowExceptionIfLandmarkNotFound() {
            Landmark retrieved = landmarks.GetById(32);
        }

        [TestMethod]
        [ExpectedException(typeof(TourNotFoundException))]
        public void ShouldThrowExceptionInGettingLandmarksOfUnexistentTour()
        {
            landmarks.GetTourLandmarks(9);
        }

        [TestMethod]
        [ExpectedException(typeof(CorruptedDataException))]
        public void ShouldThrowExceptionWhenDatabaseHasInconsistencies() {
            Mock<ISqlContext> fakeContext = new Mock<ISqlContext>();
            Dictionary<string, object> faultyLandmarkData = new Dictionary<string, object>();
            faultyLandmarkData.Add("ID", -1);
            faultyLandmarkData.Add("TITLE", "");
            faultyLandmarkData.Add("LATITUDE", -34.923844);
            faultyLandmarkData.Add("LONGITUDE", -56.170590);
            faultyLandmarkData.Add("DESCRIPT", "");
            faultyLandmarkData.Add("EXTENSION", "");
            ICollection<Dictionary<string, object>> fakeReturn = new List<Dictionary<string, object>>() { faultyLandmarkData };
            fakeContext.Setup(c => c.ExcecuteRead(It.IsAny<string>())).Returns(fakeReturn);
            landmarks = new SqlServerLandmarksRepository(fakeContext.Object, testData.LandmarksImagesPath, testData.AudiosPath);
            landmarks.GetById(2);
        }
    }
}