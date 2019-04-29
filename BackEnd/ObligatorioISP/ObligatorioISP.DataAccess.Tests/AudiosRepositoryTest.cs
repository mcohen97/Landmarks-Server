using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Tests
{
    [TestClass]
    public class AudiosRepositoryTest
    {

        private IAudiosRepository audios;
        private string audiosDirectory;

        [TestInitialize]
        public void StartUp() {
            audios = new DiskAudiosRepository();
        }
    }
}
