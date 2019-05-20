using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace ObligatorioISP.DataAccess
{
    internal class StreamReader
    {
        public string GetResource(string path) {
            byte[] data;
            try
            {
                data = TryRead(path);
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
