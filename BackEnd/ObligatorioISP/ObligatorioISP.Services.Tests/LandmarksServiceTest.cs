using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using System.Collections.Generic;
using System.IO;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]
    public class LandmarksServiceTest
    {
        private ILandmarksService service;
        private Mock<ILandmarksRepository> landmarks;

        [TestInitialize]
        public void SetUp() {
            landmarks = new Mock<ILandmarksRepository>();
            landmarks.Setup(r => r.GetTourLandmarks(It.IsAny<int>())).Returns(GetFakeLandmarks());
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>())).Returns(GetFakeLandmarks());
            service = new LandmarksService(landmarks.Object);
        }

        [TestMethod]
        public void ShouldReturnLandmarksFromRepository() {
            double lat = -34.923844;
            double lng = -56.170590;
            double dist = 2;

            ICollection<LandmarkDto> retrieved = service.GetLandmarksWithinZone(lat, lng, dist);
            landmarks.Verify(l => l.GetWithinZone(lat, lng, dist), Times.Once);
            Assert.AreEqual(GetFakeLandmarks().Count, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnTourLandmarksFromRepository() {
            int id = 1;
            ICollection<LandmarkDto> retrieved = service.GetLandmarksOfTour(id);
            landmarks.Verify(l => l.GetTourLandmarks(id),Times.Once);
            Assert.AreEqual(GetFakeLandmarks().Count, retrieved.Count);
        }

        private ICollection<Landmark> GetFakeLandmarks()
        {
            string testImage = "testImage.jpg";
            if (!File.Exists(testImage)) {
                File.Create(testImage);
            }
            ICollection<Landmark> sampleList = new List<Landmark>() {
                new Landmark(1, "Landmark 1",-34.912126,-56.167282,"Description 1", testImage),
                new Landmark(2,"Landmark 2",-34.912900,-56.162263,"Description 2",testImage),
                new Landmark(3,"Landmark 3",-34.914202,-56.157930,"Description 3",testImage),
                new Landmark(4,"Landmark 4", -34.910866,-56.183353,"Description 4",testImage)
            };
            return sampleList;
        }
    }
}
