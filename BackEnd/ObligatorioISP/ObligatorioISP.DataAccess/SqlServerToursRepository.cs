

using System;
using System.Collections.Generic;
using System.Linq;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;
using ObligatorioISP.DataAccess.Contracts.Exceptions;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerToursRepository: IToursRepository
    {
        private string connectionString;
        private ILandmarksRepository landmarks;

        private SqlServerConnectionManager connection;

        public SqlServerToursRepository(string aConnectionString, ILandmarksRepository aRepository) {
            connectionString = aConnectionString;
            landmarks = aRepository;
            connection = new SqlServerConnectionManager(aConnectionString);
        }

        public TourDto GetById(int id)
        {
            string command = $"SELECT * FROM Tour WHERE ID = {id};";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            if (!rows.Any()) {
                throw new TourNotFoundException("Tour not found");
            }
            ICollection<TourDto> result = rows.Select(r => BuildTour(r)).ToList();
            return result.First();
        }

        public ICollection<TourDto> GetToursWithinKmRange(double centerLat, double centerLng, double rangeInKm)
        {
            string command = $"SELECT T.* FROM Tour T " 
                + $"WHERE NOT EXISTS (" 
                + $"SELECT 1 FROM Landmark L, LandmarkTour LT" 
                + $" WHERE L.ID = LT.LANDMARK_ID AND T.ID = LT.TOUR_ID " 
                + $"AND dbo.DISTANCE({centerLat},{centerLng},L.LATITUDE, L.LONGITUDE) > {rangeInKm});";
       
            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<TourDto> result = rows.Select(r => BuildTour(r)).ToList();
            return result;
        }

        private TourDto BuildTour(Dictionary<string, object> rawData)
        {
            int tourId = Int32.Parse(rawData["ID"].ToString());
            string title = rawData["TITLE"].ToString();
            ICollection<LandmarkDto> tourStops = landmarks.GetTourLandmarks(tourId);
            TourDto tour = new TourDto() {Id=tourId };
            return tour;
        }
    }
}
