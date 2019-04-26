using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services.Contracts
{
    public interface IImagesService
    {
        string GetImageInBase64(string imageName);
    }
}
