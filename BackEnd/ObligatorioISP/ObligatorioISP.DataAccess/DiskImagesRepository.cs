using ObligatorioISP.DataAccess.Contracts;
using System;
using System.IO;

namespace ObligatorioISP.DataAccess
{
    public class DiskImagesRepository : IImagesRepository
    {
        private StreamReader reader;

        public DiskImagesRepository() {
            reader = new StreamReader();
        }

        public string GetImageInBase64(string imageName)
        {
            return reader.GetResource(imageName);
        }
    }
}
