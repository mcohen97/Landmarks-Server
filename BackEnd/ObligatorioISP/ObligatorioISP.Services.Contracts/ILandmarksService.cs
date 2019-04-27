using ObligatorioISP.Services.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services.Contracts
{
    public interface ILandmarksService
    {
        ICollection<LandmarkDto> GetLandmarksWithinZone(double latitude, double longitude, double distance);
        ICollection<LandmarkDto> GetLandmarksOfTour(int id);
    }
}
