using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.BusinessLogic.Exceptions;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.IO;

namespace ObligatorioISP.BusinessLogic.Tests
{
    [ExcludeFromCodeCoverage]
    [TestClass]
    public class LandmarkTest
    {

        [TestInitialize]
        public void SetUp()
        {
            if (!File.Exists("testImage1.jpg"))
            {
                File.Create("testImage1.jpg");
            }
            if (!File.Exists("testImage1.jpg"))
            {
                File.Create("testAudio.mp3");
            }
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfIdIsNegative()
        {
            Landmark landmark = new Landmark(-1, "title", "description", new List<string>() { "testImage1.jpg" }, 0.0, 0.0);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionWhenTitleIsNullOrEmpty()
        {
            Landmark landmark = new Landmark(1,"", 0.0, 0.0, "description", "iconPath");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionWhenDescriptionIsNull()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, null, "iconPath");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionWhenPathDoesNotExist()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "iconPath");
        }

        [TestMethod]
        public void ShouldCreateLandmarkIfDataIsValid()
        {
            Landmark landmark = new Landmark(1,"title", 0.0, 0.0, "description", "testImage1.jpg");
            Assert.AreEqual("title", landmark.Title);
            Assert.AreEqual("description", landmark.Description);
            Assert.AreEqual(1, landmark.Id);
        }

        [TestMethod]
        public void ShouldHaveId0IfDataIsValidAndNoIdAssigned()
        {
            Landmark landmark = new Landmark(0, "title", 0.0, 0.0, "description", "testImage1.jpg");
            Assert.AreEqual(0, landmark.Id);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfImageListIsEmpty()
        {
            Landmark landmark = new Landmark(1,"title", "description", new List<string>(), 0.0, 0.0);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfImageListHasUnexistentPath()
        {
            Landmark landmark = new Landmark(1,"title", "description", new List<string>() { "iconImage" }, 0.0, 0.0);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfImageListIsNull()
        {
            Landmark landmark = new Landmark(1,"title", "description", null, 0.0, 0.0);
        }

        [TestMethod]
        public void ShouldReturnTheImagesPathsWhenAsked()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage1.jpg");
            List<string> images = landmark.Images;
            Assert.AreEqual(1, images.Count);
            Assert.AreEqual("testImage1.jpg", images[0]);
        }

        [TestMethod]
        public void ShouldBeOneMoreImageWhenOneAdded()
        {
            File.Create("testImage2.jpg");
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage1.jpg");
            landmark.AddImage("testImage2.jpg");
            Assert.AreEqual(2, landmark.Images.Count);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfUnexistentImageIsAdded()
        {
            Landmark landmark = new Landmark(1,"title", "description", new List<string>() { "testImage1.jpg" }, 0.0, 0.0);
            landmark.AddImage("unexistentImage.jpg");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfAudioListIsNull()
        {
            Landmark landmark = new Landmark(1,"title", 0.0, 0.0, "description", new List<string>() { "testImage1.jpg" }, null);
        }

        [TestMethod]
        public void ShouldReturnAudiosOfConstructor()
        {
            Landmark landmark = new Landmark(1, "title", 0.0, 0.0, "description", new List<string>() { "testImage1.jpg" }, new List<string>() { "testAudio.mp3" });
            Assert.AreEqual(1, landmark.Audios.Count);
        }

        [TestMethod]
        public void ShouldHaveCeroAudioguidesWhenCreated()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage1.jpg");
            Assert.AreEqual(0, landmark.Audios.Count);
        }

        [TestMethod]
        public void ShouldBeOneMoreAudioWhenOneAdded()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage1.jpg");
            landmark.AddAudio("testAudio.mp3");
            Assert.AreEqual(1, landmark.Audios.Count);
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExceptionIfUnexistentAudioIsAdded()
        {
            Landmark landmark = new Landmark("title", 0.0, 0.0, "description", "testImage1.jpg");
            landmark.AddAudio("unexistentAudio.mp3");
        }

        [TestMethod]
        [ExpectedException(typeof(InvalidLandmarkException))]
        public void ShouldThrowExcetpionIfOneOfTheAudiosIsUnexistent()
        {
            Landmark landmark = new Landmark(1,"title", 0.0, 0.0, "description", new List<string>() { "testImage1.jpg" }, new List<string>() { "testAudio.mp3", "unexistentAudio.mp3" });
        }

    }
}
