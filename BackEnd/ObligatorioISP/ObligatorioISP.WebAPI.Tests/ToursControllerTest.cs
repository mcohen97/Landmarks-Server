using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using ObligatorioISP.WebAPI.Controllers;
using System;
using System.Collections.Generic;
using System.Linq;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class ToursControllerTest
    {
        private ToursController controller;
        private Mock<IToursService> fakeToursStorage;

        [TestInitialize]
        public void StartUp()
        {
            fakeToursStorage = new Mock<IToursService>();
            fakeToursStorage.Setup(r => r.GetTourById(It.IsAny<int>())).Returns((int x) => GetFakeTours().FirstOrDefault(t => t.Id == x));
            fakeToursStorage.Setup(r => r.GetToursWithinKmRange(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>())).Returns(GetFakeTours());
            controller = new ToursController(fakeToursStorage.Object);
        }

        [TestMethod]
        public void ShouldReturnToursFromStorage()
        {
            double centerLat = -34.912127;
            double centerLng = -56.167283;
            double distance = 2;

            IActionResult result = controller.Get(centerLat, centerLng, distance);
            OkObjectResult ok = result as OkObjectResult;
            List<TourDto> tours = ok.Value as List<TourDto>;

            fakeToursStorage.Verify(r => r.GetToursWithinKmRange(centerLat, centerLng, distance));
            Assert.IsNotNull(result);
            Assert.IsNotNull(ok);
            Assert.AreEqual(200, ok.StatusCode);
            Assert.IsNotNull(tours);
            Assert.AreEqual(GetFakeTours().Count, tours.Count);
        }

        [TestMethod]
        public void ShouldReturnTheTourOfIdGiven()
        {
            int id = 1;

            IActionResult result = controller.Get(id);
            OkObjectResult ok = result as OkObjectResult;
            TourDto tour = ok.Value as TourDto;

            fakeToursStorage.Verify(r => r.GetTourById(id));
            Assert.IsNotNull(result);
            Assert.IsNotNull(ok);
            Assert.AreEqual(200, ok.StatusCode);
            Assert.IsNotNull(tour);
            Assert.AreEqual(id, tour.Id);
        }

        [TestMethod]
        public void ShouldReturn404IfTourNotFound() {
            Exception internalEx = new TourNotFoundException();
            Exception toThrow = new ServiceException(internalEx.Message, ErrorType.ENTITY_NOT_FOUND);
            fakeToursStorage.Setup(s => s.GetTourById(It.IsAny<int>())).Throws(toThrow);

            IActionResult result = controller.Get(2);
            ObjectResult notFound = result as ObjectResult;
            ErrorDto error = notFound.Value as ErrorDto;

            Assert.IsNotNull(result);
            Assert.IsNotNull(notFound);
            Assert.AreEqual(404, notFound.StatusCode);
            Assert.IsNotNull(error);
            Assert.AreEqual(toThrow.Message, error.ErrorMessage);
        }

        [TestMethod]
        public void ShouldReturn500IfCantAccessData()
        {
            Exception internalEx = new DataInaccessibleException();
            Exception toThrow = new ServiceException(internalEx.Message, ErrorType.DATA_INACCESSIBLE);
            fakeToursStorage.Setup(s => s.GetTourById(It.IsAny<int>())).Throws(toThrow);

            IActionResult result = controller.Get(2);
            ObjectResult notFound = result as ObjectResult;
            ErrorDto error = notFound.Value as ErrorDto;

            Assert.IsNotNull(result);
            Assert.IsNotNull(notFound);
            Assert.AreEqual(500, notFound.StatusCode);
            Assert.IsNotNull(error);
            Assert.AreEqual(toThrow.Message, error.ErrorMessage);
        }

        private ICollection<TourDto> GetFakeTours()
        {
            return new List<TourDto>() {
                new TourDto(){
                Id=1},
                new TourDto(){
                    Id=2
                },
                new TourDto(){
                    Id = 3
                }
            };
        }
    }
}
