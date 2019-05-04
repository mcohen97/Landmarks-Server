using ObligatorioISP.DataAccess.Contracts;
using System;
using System.IO;

namespace ObligatorioISP.DataAccess
{
    public class DiskAudiosRepository : IAudiosRepository
    {
        public string GetAudioInBase64(string audioPath)
        {
            byte[] data;
            try
            {
                data = TryRead(audioPath);
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
