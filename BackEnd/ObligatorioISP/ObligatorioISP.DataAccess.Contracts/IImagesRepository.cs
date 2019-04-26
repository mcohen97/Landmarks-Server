using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface IImagesRepository
    {
        string GetImageInBase64(string imageName);
    }
}
