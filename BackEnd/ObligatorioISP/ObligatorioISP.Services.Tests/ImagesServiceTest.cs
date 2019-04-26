using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;

namespace ObligatorioISP.Services.Tests
{
    [TestClass]
    public class ImagesServiceTest
    {

        private string imagesDirectory;
        private string testImageName;
        private string pixelImageBase64;

        private IImagesService service;

        [TestInitialize]
        public void StartUp() {
            testImageName = "testImage.jpg";
            imagesDirectory = "Images";
            pixelImageBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
            service = new DiskImageService(imagesDirectory);
            WriteTestImage();
        }

        [TestMethod]
        public void ShouldGetExistentImage() {

        }

        private void WriteTestImage() {
            byte[] bytes = Convert.FromBase64String(pixelImageBase64);
            string imagePath = imagesDirectory + "/" + testImageName;
            using (var imageFile = new FileStream(imagePath, FileMode.Create))
            {
                imageFile.Write(bytes, 0, bytes.Length);
                imageFile.Flush();
            }
        }
    }
}
