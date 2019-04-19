using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using ObligatorioISP.WebAPI.Controllers;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class LandmarkControllerTest
    {
        private LandmarksController controller;
        private Mock<ILandmarksRepository> landmarks;
    
        [TestInitialize]
        public void SetUp() {
            landmarks = new Mock<ILandmarksRepository>();
            landmarks.Setup(l => l.GetWithinCoordenates(It.IsAny<int>(), It.IsAny<int>(), It.IsAny<int>(), It.IsAny<int>()))
                .Returns(GetFakeLandmarks());
            controller = new LandmarksController(landmarks.Object);
        }

        [TestMethod]
        public void ShouldReturn200OKWhenGET() {

            double bottomLeftLat = -34.923844;
            double bottomLeftLng = -56.170590;
            double topRightLat = -34.908501;
            double topRightLng = -56.155019;

            IActionResult result = controller.Get(bottomLeftLat, bottomLeftLng, topRightLat, topRightLng);
            OkResult ok = result as OkResult;
            ICollection<LandmarkDto> landmarks = ok.Value as ICollection<LandmarkDto>;

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
