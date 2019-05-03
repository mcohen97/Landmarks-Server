using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using ObligatorioISP.WebAPI.Controllers;

namespace ObligatorioISP.WebAPI.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class LandmarksControllerTest
    {
        private LandmarksController controller;
        private Mock<ILandmarksService> fakeLandmarksService;
    
        [TestInitialize]
        public void SetUp() {
            fakeLandmarksService = new Mock<ILandmarksService>();
            fakeLandmarksService.Setup(l => l.GetLandmarksWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Returns(GetFakeSummarizedLandmarks());
            fakeLandmarksService.Setup(l => l.GetLandmarkById(It.IsAny<int>()))
                .Returns((int id) => GetFakeDetailedLandmarks().First(lm => lm.Id == id));
            fakeLandmarksService.Setup(l => l.GetLandmarksOfTour(It.IsAny<int>()))
                .Returns(GetFakeSummarizedLandmarks());
            controller = new LandmarksController(fakeLandmarksService.Object);
        }

        [TestMethod]
        public void ShouldReturn200OKWhenGET() {

            double centerLat = -34.923844;
            double centerLng = -56.170590;
            double distance = 2;

            IActionResult result = controller.Get(centerLat, centerLng, distance);
            OkObjectResult ok = result as OkObjectResult;
            ICollection<LandmarkSummarizedDto> landmarks = ok.Value as ICollection<LandmarkSummarizedDto>;

            fakeLandmarksService.Verify(r => r.GetLandmarksWithinZone(centerLat, centerLng, distance), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(ok);
            Assert.AreEqual(200, ok.StatusCode);
            Assert.IsNotNull(landmarks);
            Assert.AreEqual(GetFakeDetailedLandmarks().Count,landmarks.Count);
        }

        [TestMethod]
        public void ShouldReturnTheDtosAsRetrieved() {

            double centerLat = -34.912127;
            double centerLng = -56.167283;

            IActionResult result = controller.Get(centerLat, centerLng, 2);
            OkObjectResult ok = result as OkObjectResult;
            List<LandmarkSummarizedDto> landmarks = ok.Value as List<LandmarkSummarizedDto>;
            LandmarkSummarizedDto firstLandmark = landmarks[0];

            Assert.AreEqual(1, firstLandmark.Id);
            Assert.AreEqual("Landmark 1", firstLandmark.Title);
            Assert.AreEqual(-34.912126, firstLandmark.Latitude);
            Assert.AreEqual(-56.167282, firstLandmark.Longitude);
            Assert.AreEqual("image1", firstLandmark.IconBase64);
        }

        [TestMethod]
        public void ShouldReturnTheLandmarkWithTheIdInGET() {
            int id = 2;

            IActionResult result = controller.Get(id);
            OkObjectResult ok = result as OkObjectResult;
            LandmarkDetailedDto landmark = ok.Value as LandmarkDetailedDto;

            fakeLandmarksService.Verify(r => r.GetLandmarkById(id), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(ok);
            Assert.AreEqual(200, ok.StatusCode);
            Assert.IsNotNull(landmark);
        }

        [TestMethod]
        public void ShouldReturn404IfLandmarkDoesNotExist() {
            Exception internalEx = new LandmarkNotFoundException();
            Exception toThrow = new ServiceException(internalEx.Message, ErrorType.ENTITY_NOT_FOUND);
            fakeLandmarksService.Setup(s => s.GetLandmarkById(It.IsAny<int>())).Throws(toThrow);

            IActionResult result = controller.Get(2);
            ObjectResult notFound = result as ObjectResult;
            ErrorDto error = notFound.Value as ErrorDto;

            fakeLandmarksService.Verify(l => l.GetLandmarkById(2), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(notFound);
            Assert.AreEqual(404, notFound.StatusCode);
            Assert.IsNotNull(error);
            Assert.AreEqual(toThrow.Message, error.ErrorMessage);
        }

        [TestMethod]
        public void ShouldReturn500IfCantAccessDataInGET() {
            Exception internalEx = new DataInaccessibleException();
            Exception toThrow = new ServiceException(internalEx.Message, ErrorType.DATA_INACCESSIBLE);
            fakeLandmarksService.Setup(s => s.GetLandmarkById(It.IsAny<int>())).Throws(toThrow);

            IActionResult result = controller.Get(2);
            ObjectResult internalServerError = result as ObjectResult;
            ErrorDto error = internalServerError.Value as ErrorDto;

            fakeLandmarksService.Verify(l => l.GetLandmarkById(2), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(internalServerError);
            Assert.AreEqual(500, internalServerError.StatusCode);
            Assert.IsNotNull(error);
            Assert.AreEqual(toThrow.Message, error.ErrorMessage);
        }

        [TestMethod]
        public void ShouldReturn200WhenSuccessfulGETFromTour()
        {
            int id = 2;

            IActionResult result = controller.GetByTour(id);
            OkObjectResult ok = result as OkObjectResult;
            ICollection<LandmarkSummarizedDto> landmarks = ok.Value as ICollection<LandmarkSummarizedDto>;

            fakeLandmarksService.Verify(r => r.GetLandmarksOfTour(id), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(ok);
            Assert.AreEqual(200, ok.StatusCode);
            Assert.IsNotNull(landmarks);
            Assert.AreEqual(GetFakeSummarizedLandmarks().Count, landmarks.Count);
        }

        [TestMethod]
        public void ShouldReturn404WhenGETFromUnexistentTour()
        {
            Exception internalEx = new TourNotFoundException();
            Exception toThrow = new ServiceException(internalEx.Message, ErrorType.ENTITY_NOT_FOUND);
            fakeLandmarksService.Setup(s => s.GetLandmarksOfTour(It.IsAny<int>())).Throws(toThrow);

            IActionResult result = controller.GetByTour(2);
            ObjectResult internalServerError = result as ObjectResult;
            ErrorDto error = internalServerError.Value as ErrorDto;

            fakeLandmarksService.Verify(l => l.GetLandmarksOfTour(2), Times.Once);
            Assert.IsNotNull(result);
            Assert.IsNotNull(internalServerError);
            Assert.AreEqual(404, internalServerError.StatusCode);
            Assert.IsNotNull(error);
            Assert.AreEqual(toThrow.Message, error.ErrorMessage);
        }

        private ICollection<LandmarkDetailedDto> GetFakeDetailedLandmarks()
        {
            ICollection<LandmarkDetailedDto> sampleList = new List<LandmarkDetailedDto>() {
                new LandmarkDetailedDto(){
                    Id = 1,
                    Title = "Landmark 1",
                    Latitude = -34.912126,
                    Longitude =-56.167282,
                    Description = "Description 1",
                    ImagesBase64 = new List<string>(){"image1","image2","image3" }
                },
                new LandmarkDetailedDto(){
                    Id = 2,
                    Title = "Landmark 2",
                    Latitude = -34.912900,
                    Longitude =-56.162263,
                    Description = "Description 2",
                    ImagesBase64 = new List<string>(){"image1","image2","image3" }
                },
                new LandmarkDetailedDto(){
                    Id = 3,
                    Title = "Landmark 3",
                    Latitude = -34.914202,
                    Longitude =-56.157930,
                    Description = "Description 3",
                    ImagesBase64 = new List<string>(){"image1","image2","image3" }
                },
                new LandmarkDetailedDto(){
                    Id = 4,
                    Title = "Landmark 4",
                    Latitude = -34.910866,
                    Longitude =-56.183353,
                    Description = "Description 4",
                    ImagesBase64 = new List<string>(){"image1","image2","image3" }
                }

            };
            return sampleList;
        }

        private ICollection<LandmarkSummarizedDto> GetFakeSummarizedLandmarks() {
            return GetFakeDetailedLandmarks().Select(l => new LandmarkSummarizedDto()
            {
                Id = l.Id,
                Title = l.Title,
                Latitude = l.Latitude,
                Longitude = l.Longitude,
                IconBase64 = l.ImagesBase64.First()
            }).ToList();
        }
    }
}
