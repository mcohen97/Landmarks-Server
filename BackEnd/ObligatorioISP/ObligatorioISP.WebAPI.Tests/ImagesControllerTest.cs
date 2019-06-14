using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.WebAPI.Controllers;
using System.IO;
using System.Threading.Tasks;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class ImagesControllerTest
    {
        private ImagesController controller;
        private Mock<IConfiguration> config;
        private string landmarksConfigKey;
        private string toursConfigKey;
        private string testImage;

        [TestInitialize]
        public void StartUp() {
            landmarksConfigKey = "LandmarkImages:Uri";
            toursConfigKey = "TourImages:Uri";
            config = new Mock<IConfiguration>();
            config.Setup(c => c[landmarksConfigKey]).Returns(Directory.GetCurrentDirectory());
            config.Setup(c => c[toursConfigKey]).Returns(Directory.GetCurrentDirectory());
            testImage = "testImage.jpg";
            if (!File.Exists(testImage)) {
                File.Create(testImage);
            }
            controller = new ImagesController(config.Object);
        }

        [TestMethod]
        public void GetExistentLandmarkImage() {
            Task<IActionResult> task =controller.GetLandmarkImage(testImage);
            task.Wait();
            IActionResult result = task.Result;
            FileStreamResult stream = result as FileStreamResult;

            Assert.IsNotNull(stream);
            Assert.AreEqual("image/jpeg",stream.ContentType);
        }

        [TestMethod]
        public void GetExistentTourImage()
        {
            Task<IActionResult> task = controller.GetTourImage(testImage);
            task.Wait();
            IActionResult result = task.Result;
            FileStreamResult stream = result as FileStreamResult;

            Assert.IsNotNull(stream);
            Assert.AreEqual("image/jpeg", stream.ContentType);
        }

        [TestMethod]
        public void GerUnexistentImage() {
            Task<IActionResult> task = controller.GetLandmarkImage("unexistent.jpg");
            task.Wait();

            IActionResult result = task.Result;
            ObjectResult error = result as ObjectResult;

            Assert.IsNotNull(error);
            Assert.AreEqual(404, error.StatusCode);
        }
    }
}
