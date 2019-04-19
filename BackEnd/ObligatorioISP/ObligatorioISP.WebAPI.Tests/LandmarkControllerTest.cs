using System;
using System.Collections.Generic;
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

        }

        private ICollection<LandmarkDto> GetFakeLandmarks()
        {
            throw new NotImplementedException();
        }
    }
}
