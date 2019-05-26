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
        private string configKey;
        private string testImage;

        [TestInitialize]
        public void StartUp() {
            configKey = "LandmarkImages:Uri";
            config = new Mock<IConfiguration>();
            config.Setup(c => c[configKey]).Returns(Directory.GetCurrentDirectory());
            testImage = "testImage.jpg";
            if (!File.Exists(testImage)) {
                File.Create(testImage);
            }
            controller = new ImagesController(config.Object);
        }

        [TestMethod]
        public void GetExistentImage() {
            Task<IActionResult> task =controller.Get(testImage);
            task.Wait();
            IActionResult result = task.Result;
            FileStreamResult stream = result as FileStreamResult;

            Assert.IsNotNull(stream);
            Assert.AreEqual("image/jpeg",stream.ContentType);
        }

        [TestMethod]
        public void GerUnexistentImage() {
            Task<IActionResult> task = controller.Get("unexistent.jpg");
            task.Wait();

            IActionResult result = task.Result;
            ObjectResult error = result as ObjectResult;

            Assert.IsNotNull(error);
            Assert.AreEqual(404, error.StatusCode);
        }
    }
}
