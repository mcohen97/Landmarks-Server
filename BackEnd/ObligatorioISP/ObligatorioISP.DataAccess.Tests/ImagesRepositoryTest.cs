using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.DataAccess.Contracts;
using System;
using System.IO;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class ImagesServiceTest
    {

        private string imagesDirectory;
        private string testImageName;
        private string pixelImageBase64;
        private string imagePath;

        private IImagesRepository service;

        [TestInitialize]
        public void StartUp() {
            testImageName = "testImage.jpg";

            imagesDirectory = "Images";
            Directory.CreateDirectory(imagesDirectory);
            imagePath = imagesDirectory + "/" + testImageName;


            pixelImageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
            service = new DiskImagesRepository();
            WriteTestImage();
        }

        [TestMethod]
        public void ShouldGetExistentImage() {
            string image =service.GetImageInBase64(imagePath);
            Assert.AreEqual(pixelImageBase64, image);
        }

        private void WriteTestImage() {
            byte[] bytes = Convert.FromBase64String(pixelImageBase64);
            using (var imageFile = new FileStream(imagePath, FileMode.Create))
            {
                imageFile.Write(bytes, 0, bytes.Length);
                imageFile.Flush();
            }
        }
    }
}
