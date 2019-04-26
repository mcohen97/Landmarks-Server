using System;
using System.Collections.Generic;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using ObligatorioISP.Services.Contracts.Dtos;

namespace ObligatorioISP.Services
{
    public class ToursService : IToursService
    {
        private IToursRepository tours;
        private ILandmarksRepository landmarks;

        public ToursService(IToursRepository toursStorage, ILandmarksRepository landmarksStorage) {
            tours = toursStorage;
            landmarks = landmarksStorage;
        }

        public TourDto GetTourById(int id)
        {
            Tour retrievedTour = tours.GetById(id);
            TourDto dto = BuildDto(retrievedTour);
            return dto;
        }

        public ICollection<TourDto> GetToursWithinKmRange(double lat, double lng, double distance)
        {
            ICollection<Tour> retrievedTours = tours.GetToursWithinKmRange(lat, lng, distance);
            return retrievedTours.Select(t => BuildDto(t)).ToList();
        }

        private TourDto BuildDto(Tour retrieved)
        {
            TourDto conversion = new TourDto
            {
                Id = retrieved.Id,
                Title = retrieved.Title,
                LandmarksIds = retrieved.Landmarks.Select(t => t.Id)
            };
            return conversion;
        }
    }
}
