using System;
using System.Collections.Generic;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.BusinessLogic.Exceptions;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerToursRepository: IToursRepository
    {
        private ILandmarksRepository landmarks;

        private ISqlContext connection;

        public SqlServerToursRepository(ISqlContext context, ILandmarksRepository aRepository) {
            landmarks = aRepository;
            connection = context;
        }

        public Tour GetById(int id)
        {
            string command = $"SELECT * FROM Tour WHERE ID = {id};";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            if (!rows.Any()) {
                throw new TourNotFoundException();
            }
            ICollection<Tour> result = rows.Select(r => BuildTour(r)).ToList();
            return result.First();
        }

        public ICollection<Tour> GetToursWithinKmRange(double centerLat, double centerLng, double rangeInKm)
        {
            string command = $"SELECT T.* FROM Tour T " 
                + $"WHERE NOT EXISTS (" 
                + $"SELECT 1 FROM Landmark L, LandmarkTour LT" 
                + $" WHERE L.ID = LT.LANDMARK_ID AND T.ID = LT.TOUR_ID " 
                + $"AND dbo.DISTANCE({centerLat},{centerLng},L.LATITUDE, L.LONGITUDE) > {rangeInKm});";
       
            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<Tour> result = rows.Select(r => BuildTour(r)).ToList();
            return result;
        }

        private Tour BuildTour(Dictionary<string, object> rawData)
        {
            int tourId = Int32.Parse(rawData["ID"].ToString());
            string title = rawData["TITLE"].ToString();
            ICollection<Landmark> tourStops = landmarks.GetTourLandmarks(tourId);
            Tour tour;
            try
            {
                tour = new Tour(tourId, title, tourStops);
            }
            catch (InvalidTourException) {
                throw new CorruptedDataException();
            }
            return tour;
        }
    }
}
