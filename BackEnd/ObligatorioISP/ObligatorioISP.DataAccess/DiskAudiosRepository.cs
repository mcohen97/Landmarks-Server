using ObligatorioISP.DataAccess.Contracts;
using System;
using System.IO;

namespace ObligatorioISP.DataAccess
{
    public class DiskAudiosRepository : IAudiosRepository
    {
        private StreamReader reader;

        public DiskAudiosRepository() {
            reader = new StreamReader();
        }

        public string GetAudioInBase64(string audioPath)
        {
            return reader.GetResource(audioPath);
        }

    }
}
