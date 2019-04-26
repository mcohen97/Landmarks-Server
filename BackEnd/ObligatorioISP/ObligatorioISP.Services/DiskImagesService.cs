using ObligatorioISP.Services.Contracts;
using System;
using System.IO;

namespace ObligatorioISP.Services
{
    public class DiskImagesService : IImagesService
    {

        private string directory;
        public DiskImagesService(string imagesFolder)
        {
            directory = imagesFolder;
        }

        public string GetImageInBase64(string imageName)
        {
            byte[] data;
            try
            {
                data = TryRead(imageName);
            }
            catch (IOException)
            {
                data = new byte[0];
            }
            return Convert.ToBase64String(data);
        }

        private byte[] TryRead(string path)
        {
            byte[] bytes = new byte[0];
            string fullPath = directory + "/" + path;
            using (Stream source = File.OpenRead(fullPath))
            {
                bytes = new byte[source.Length];
                source.Read(bytes, 0, bytes.Length);
            }
            return bytes;
        }
    }
}
