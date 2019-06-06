using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.WebAPI.Controllers;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class LocationControllerTest
    {
        private Mock<IProximityNotificationService> fakeService;
        private LocationController controller;

        [TestInitialize]
        public void SetUp() {
            fakeService = new Mock<IProximityNotificationService>();
            controller = new LocationController(fakeService.Object);
        }


    }
}
