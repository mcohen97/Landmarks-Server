using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.WebAPI.Controllers;

namespace ObligatorioISP.WebAPI.Tests
{
    [TestClass]
    public class AudiosControllerTest
    {
        private AudiosController controller;
        private Mock<IConfiguration> config;
        private string configKey;
        private string testAudioName;

        [TestInitialize]
        public void StartUp()
        {
            configKey = "Audios:Uri";
            config = new Mock<IConfiguration>();
            config.Setup(c => c[configKey]).Returns(Directory.GetCurrentDirectory());
            testAudioName = "testAudio.mp3";
            if (!File.Exists(testAudioName))
            {
                File.Create(testAudioName);
            }
            controller = new AudiosController(config.Object);
        }

        [TestMethod]
        public void GetExistentAudio()
        {
            Task<IActionResult> task = controller.GetLandmarkAudio(testAudioName);
            task.Wait();
            IActionResult result = task.Result;
            FileStreamResult stream = result as FileStreamResult;

            Assert.IsNotNull(stream);
            Assert.AreEqual("audio/mp3", stream.ContentType);
        }

        [TestMethod]
        public void GerUnexistenAudio()
        {
            Task<IActionResult> task = controller.GetLandmarkAudio("unexistent.mp3");
            task.Wait();

            IActionResult result = task.Result;
            ObjectResult error = result as ObjectResult;

            Assert.IsNotNull(error);
            Assert.AreEqual(404, error.StatusCode);
        }
    }
}
