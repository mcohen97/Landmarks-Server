﻿using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;
using ObligatorioISP.Services.Contracts.Exceptions;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]
    public class LandmarksServiceTest
    {
        private ILandmarksService service;
        private Mock<ILandmarksRepository> landmarks;
        private Mock<IImagesRepository> images;
        private Mock<IAudiosRepository> audios;

        private string testImageData;
        private string testAudioData;

        [TestInitialize]
        public void SetUp() {
            SetUpRepositories();
            service = new LandmarksService(landmarks.Object, images.Object, audios.Object);
        }

        private void SetUpRepositories() {
            testImageData = "imageData";
            testAudioData = "audioData";
            images = new Mock<IImagesRepository>();
            images.Setup(i => i.GetImageInBase64(It.IsAny<string>())).Returns(testImageData);
            audios = new Mock<IAudiosRepository>();
            audios.Setup(a => a.GetAudioInBase64(It.IsAny<string>())).Returns(testAudioData);
            landmarks = new Mock<ILandmarksRepository>();
            landmarks.Setup(r => r.GetTourLandmarks(It.IsAny<int>())).Returns(GetFakeLandmarks());
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>())).Returns(GetFakeLandmarks());
            landmarks.Setup(r => r.GetById(It.IsAny<int>())).Returns((int id) => GetFakeLandmarks().First(l => l.Id == id));
        }  

        [TestMethod]
        public void ShouldReturnLandmarksFromRepository() {
            double lat = -34.923844;
            double lng = -56.170590;
            double dist = 2;

            ICollection<LandmarkSummarizedDto> retrieved = service.GetLandmarksWithinZone(lat, lng, dist);
            landmarks.Verify(l => l.GetWithinZone(lat, lng, dist), Times.Once);
            images.Verify(i => i.GetImageInBase64(It.IsAny<string>()), Times.Exactly(retrieved.Count));
            audios.Verify(a => a.GetAudioInBase64(It.IsAny<string>()), Times.Never);
            Assert.AreEqual(GetFakeLandmarks().Count, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnTourLandmarksFromRepository() {
            int id = 1;
            ICollection<LandmarkSummarizedDto> retrieved = service.GetLandmarksOfTour(id);
            landmarks.Verify(l => l.GetTourLandmarks(id),Times.Once);
            images.Verify(i => i.GetImageInBase64(It.IsAny<string>()), Times.Exactly(retrieved.Count));
            audios.Verify(a => a.GetAudioInBase64(It.IsAny<string>()), Times.Never);
            Assert.AreEqual(GetFakeLandmarks().Count, retrieved.Count);
        }

        [TestMethod]
        public void ShouldReturnLandmarkOfIdGivenFromRepository() {
            int id = 2;
            Landmark fake = GetFakeLandmarks().First(l => l.Id == id);
            LandmarkDetailedDto retrieved = service.GetLandmarkById(id);
            landmarks.Verify(l => l.GetById(id), Times.Once);
            images.Verify(i => i.GetImageInBase64(It.IsAny<string>()), Times.Exactly(2));
            audios.Verify(a => a.GetAudioInBase64(It.IsAny<string>()), Times.Once);
            Assert.AreEqual(fake.Title, retrieved.Title);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldThrowExceptionIfTourNotFound()
        {
            int id = 3;
            landmarks.Setup(r => r.GetTourLandmarks(It.IsAny<int>())).Throws(new TourNotFoundException());

            service.GetLandmarkById(id);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldThrowExceptionIfLandmarkNotFound()
        {
            int id = 3;
            landmarks.Setup(r => r.GetById(It.IsAny<int>())).Throws(new LandmarkNotFoundException());

            service.GetLandmarkById(id);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenCantGetDataInGetLandmarkById() {
            landmarks.Setup(r => r.GetById(It.IsAny<int>())).Throws(new DataInaccessibleException());
            service.GetLandmarkById(3);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenCantGetDataInGetLandmarksWithinZone()
        {
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Throws(new DataInaccessibleException());
            service.GetLandmarksWithinZone(-34.923844, -56.170590, 2);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenCantGetDataInGetLandmarksOfTour() {
            landmarks.Setup(r => r.GetTourLandmarks(It.IsAny<int>()))
                .Throws(new DataInaccessibleException());
            service.GetLandmarksOfTour(9);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenDataCorruptedInGetLandmarkById()
        {
            landmarks.Setup(r => r.GetById(It.IsAny<int>())).Throws(new CorruptedDataException());
            service.GetLandmarkById(3);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenDataCorruptedInGetLandmarksWithinZone()
        {
            landmarks.Setup(r => r.GetWithinZone(It.IsAny<double>(), It.IsAny<double>(), It.IsAny<double>()))
                .Throws(new CorruptedDataException());
            service.GetLandmarksWithinZone(-34.923844, -56.170590, 2);
        }

        [TestMethod]
        [ExpectedException(typeof(ServiceException))]
        public void ShouldFailWhenDataCorruptedInGetLandmarksOfTour()
        {
            landmarks.Setup(r => r.GetTourLandmarks(It.IsAny<int>()))
                .Throws(new CorruptedDataException());
            service.GetLandmarksOfTour(9);
        }

        private ICollection<Landmark> GetFakeLandmarks()
        {
            string testImage = "testImage.jpg";
            if (!File.Exists(testImage)) {
                File.Create(testImage);
            }
            string testAudio = "testAudio.mp3";
            if (!File.Exists(testAudio)) {
                File.Create(testAudio);
            }

            ICollection<string> testAudios = new List<string>() { testAudio };
            ICollection<string> testImages = new List<string>() { testImage,testImage };

            ICollection<Landmark> sampleList = new List<Landmark>() {
                new Landmark(1, "Landmark 1",-34.912126,-56.167282,"Description 1", testImage),
                new Landmark(2,"Landmark 2",-34.912900,-56.162263,"Description 2",testImages,testAudios),
                new Landmark(3,"Landmark 3",-34.914202,-56.157930,"Description 3",testImage),
                new Landmark(4,"Landmark 4", -34.910866,-56.183353,"Description 4",testImages,testAudios)
            };
            return sampleList;
        }
    }
}
