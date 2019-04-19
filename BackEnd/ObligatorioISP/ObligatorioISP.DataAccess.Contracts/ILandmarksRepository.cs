using ObligatorioISP.DataAccess.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts
{
    public interface ILandmarksRepository
    {
        ICollection<LandmarkDto> GetWithinCoordenates(double leftBottomLat, double leftBottomLng, double topRightLat, double topRightLng);
    }
}
