using ObligatorioISP.DataAccess.Contracts;
using System;
using System.IO;

namespace ObligatorioISP.DataAccess
{
    public class DiskImagesRepository : IImagesRepository
    {
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
            using (Stream source = File.OpenRead(path))
            {
                bytes = new byte[source.Length];
                source.Read(bytes, 0, bytes.Length);
            }
            return bytes;
        }
    }
}
