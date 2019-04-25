using Microsoft.AspNetCore.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using ObligatorioISP.WebAPI.Controllers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class ToursControllerTest
    {
        private ToursController controller;
        private Mock<IToursRepository> fakeToursStorage;

        [TestInitialize]
        public void StartUp()
        {
            fakeToursStorage = new Mock<IToursRepository>();
            fakeToursStorage.Setup(r => r.GetById(It.IsAny<int>())).Returns((int x) => GetFakeTours().FirstOrDefault(t => t.Id == x));
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
