using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class ToursControllerTest
    {
        private ToursController controller;
        private Mock<IToursRepository> tours;

        [TestInitialize]
        public void StartUp() {
            tours = new Mock<IToursRepository>();
            controller = new ToursController(tours.Object);
        }
    }
}
