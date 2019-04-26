using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.WebAPI.Controllers;
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
