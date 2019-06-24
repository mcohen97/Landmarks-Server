using ObligatorioISP.Services.Contracts.Dtos;
using System.Collections.Generic;

namespace ObligatorioISP.Services.Contracts
{
    public interface ILandmarksService
    {
        ICollection<LandmarkDto> GetLandmarksWithinZone(double latitude, double longitude, double distance);
        ICollection<LandmarkDto> GetLandmarksOfTour(int id);
        LandmarkDto GetLandmarkById(int id);
    }
}
