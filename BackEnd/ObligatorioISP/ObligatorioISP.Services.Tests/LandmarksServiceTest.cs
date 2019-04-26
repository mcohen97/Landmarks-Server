using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using System.Collections.Generic;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]
    public class LandmarksServiceTest
    {
        private ILandmarksService service;
        private Mock<ILandmarksRepository> landmarks;

        [TestInitialize]
        public void SetUp() {
            landmarks.Setup(r => r.GetTourLandmarks(It.IsAny<int>())).Returns(GetFakeLandmarks());
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>())).Returns(GetFakeLandmarks());
            service = new LandmarksService(landmarks.Object);
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
