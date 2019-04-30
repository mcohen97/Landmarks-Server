using Microsoft.VisualStudio.TestTools.UnitTesting;
using ObligatorioISP.DataAccess.Contracts;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class AudiosRepositoryTest
    {

        private IAudiosRepository audios;
        private string audiosDirectory;
        private string testAudioBase64;
        private string testAudioPath;

        [TestInitialize]
        public void StartUp() {
            audios = new DiskAudiosRepository();
            testAudioBase64 = File.ReadAllText(@"../../testAudio.txt");
            testAudioPath = "testAudio.mp3";
            WriteTestAudio();
        }

        [TestInitialize]
        public void ShouldGetExistentAudio() {
            string audio = audios.GetAudioInBase64(testAudioPath);
            Assert.AreEqual(testAudioBase64, audio);
        }

        private void WriteTestAudio()
        {
            byte[] bytes = Convert.FromBase64String(testAudioBase64);
            using (var imageFile = new FileStream(testAudioPath, FileMode.Create))
            {
                imageFile.Write(bytes, 0, bytes.Length);
                imageFile.Flush();
            }
        }
    }
}
