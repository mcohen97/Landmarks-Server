using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.DataAccess.Contracts;

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
            controller = new LandmarksController(landmarks);
        }
    }
}
