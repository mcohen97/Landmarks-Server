using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface IAudiosRepository
    {
        string GetAudioInBase64(string audioPath);
    }
}
