using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using ObligatorioISP.WebAPI.Controllers;

namespace ObligatorioISP.WebAPI.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class LandmarkControllerTest
    {
        private LandmarksController controller;
        private Mock<ILandmarksRepository> fakeLandmarksStorage;
    
        [TestInitialize]
        public void SetUp() {
            fakeLandmarksStorage = new Mock<ILandmarksRepository>();
            fakeLandmarksStorage.Setup(l => l.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Returns(GetFakeLandmarks());
            controller = new LandmarksController(fakeLandmarksStorage.Object);
        }

        [TestMethod]
        public void ShouldReturn200OKWhenGET() {

            double centerLat = -34.923844;
            double centerLng = -56.170590;

            IActionResult result = controller.Get(centerLat, centerLng, 2);
            OkObjectResult ok = result as OkObjectResult;
            ICollection<LandmarkDto> landmarks = ok.Value as ICollection<LandmarkDto>;

            fakeLandmarksStorage.Verify(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(ok);
            Assert.AreEqual(200, ok.StatusCode);
            Assert.IsNotNull(landmarks);
            Assert.AreEqual(GetFakeLandmarks().Count,landmarks.Count);
        }

        [TestMethod]
        public void ShouldResturnTheDtosAsRetrieved() {

            double centerLat = -34.912127;
            double centerLng = -56.167283;

            IActionResult result = controller.Get(centerLat, centerLng, 2);
            OkObjectResult ok = result as OkObjectResult;
            List<LandmarkDto> landmarks = ok.Value as List<LandmarkDto>;
            LandmarkDto firstLandmark = landmarks[0];

            Assert.AreEqual(1, firstLandmark.Id);
            Assert.AreEqual("Landmark 1", firstLandmark.Title);
            Assert.AreEqual(-34.912126, firstLandmark.Latitude);
            Assert.AreEqual(-56.167282, firstLandmark.Longitude);
            Assert.AreEqual("Description 1", firstLandmark.Description);
            Assert.AreEqual("", firstLandmark.ImageBase64);
        }

        private ICollection<LandmarkDto> GetFakeLandmarks()
        {
            ICollection<LandmarkDto> sampleList = new List<LandmarkDto>() {
                new LandmarkDto(){
                    Id = 1,
                    Title = "Landmark 1",
                    Latitude = -34.912126,
                    Longitude =-56.167282,
                    Description = "Description 1",
                    ImageBase64 = ""
                },
                new LandmarkDto(){
                    Id = 2,
                    Title = "Landmark 2",
                    Latitude = -34.912900,
                    Longitude =-56.162263,
                    Description = "Description 2",
                    ImageBase64 = ""
                },
                new LandmarkDto(){
                    Id = 3,
                    Title = "Landmark 3",
                    Latitude = -34.914202,
                    Longitude =-56.157930,
                    Description = "Description 3",
                    ImageBase64 = ""
                },
                new LandmarkDto(){
                    Id = 4,
                    Title = "Landmark 4",
                    Latitude = -34.910866,
                    Longitude =-56.183353,
                    Description = "Description 4",
                    ImageBase64 = ""
                }

            };
            return sampleList;
        }
    }
}
