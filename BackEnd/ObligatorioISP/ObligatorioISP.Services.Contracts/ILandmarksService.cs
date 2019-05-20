using ObligatorioISP.Services.Contracts.Dtos;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services.Contracts
{
    public interface ILandmarksService
    {
        ICollection<LandmarkSummarizedDto> GetLandmarksWithinZone(double latitude, double longitude, double distance);
        ICollection<LandmarkSummarizedDto> GetLandmarksOfTour(int id);
        LandmarkDetailedDto GetLandmarkById(int id);
    }
}
