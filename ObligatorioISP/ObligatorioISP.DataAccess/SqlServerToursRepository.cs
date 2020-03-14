﻿using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using ObligatorioISP.BusinessLogic;
using ObligatorioISP.BusinessLogic.Exceptions;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Exceptions;

namespace ObligatorioISP.DataAccess
{
    public class SqlServerToursRepository : IToursRepository
    {
        private ILandmarksRepository landmarks;

        private ISqlContext connection;
        private string imagesDirectory;

        public SqlServerToursRepository(ISqlContext context, ILandmarksRepository aRepository, string images)
        {
            landmarks = aRepository;
            connection = context;
            imagesDirectory = images;
        }

        public Tour GetById(int id)
        {
            string command = $"SELECT * FROM Tour WHERE ID = {id};";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            if (!rows.Any())
            {
                throw new TourNotFoundException();
            }
            ICollection<Tour> result = rows.Select(r => BuildTour(r)).ToList();
            return result.First();
        }

        public ICollection<Tour> GetToursWithinKmRange(double centerLat, double centerLng, double rangeInKm)
        {
            string centerLatStr = centerLat.ToString(CultureInfo.InvariantCulture);
            string centerLngStr = centerLng.ToString(CultureInfo.InvariantCulture);
            string rangeInKmStr = rangeInKm.ToString(CultureInfo.InvariantCulture);

            string command = $"SELECT T.* FROM Tour T "
                + $"WHERE NOT EXISTS ("
                + $"SELECT 1 FROM Landmark L, LandmarkTour LT"
                + $" WHERE L.ID = LT.LANDMARK_ID AND T.ID = LT.TOUR_ID "
                + $"AND dbo.DISTANCE({centerLatStr},{centerLngStr},L.LATITUDE, L.LONGITUDE) > {rangeInKmStr});";

            ICollection<Dictionary<string, object>> rows = connection.ExcecuteRead(command);
            ICollection<Tour> result = rows.Select(r => BuildTour(r)).ToList();
            return result;
        }

        private Tour BuildTour(Dictionary<string, object> rawData)
        {
            int tourId = Int32.Parse(rawData["ID"].ToString());
            string title = rawData["TITLE"].ToString();
            string description = rawData["DESCRIPTION"].ToString();
            Enum.TryParse(rawData["CATEGORY"].ToString(), out TourCategory category);
            char separator = Path.DirectorySeparatorChar;
            string imagePath = $"{imagesDirectory}{separator}{tourId}.{rawData["IMAGE_EXTENSION"]}";
            ICollection<Landmark> tourStops = landmarks.GetTourLandmarks(tourId);

            Tour tour;

            try
            {
                tour = new Tour(tourId, title, description, tourStops, imagePath, category);
            }
            catch (InvalidTourException)
            {
                throw new CorruptedDataException();
            }
            return tour;
        }
    }
}
