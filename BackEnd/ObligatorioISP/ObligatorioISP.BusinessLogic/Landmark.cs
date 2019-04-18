using ObligatorioISP.BusinessLogic.Exceptions;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace ObligatorioISP.BusinessLogic
{
    public class Landmark
    {
        private string title;
        public string Title { get { return title; } private set { SetTitle(value); } }

        private double latitude;
        private double longitude;
        private string description;
        public string Description { get { return description; } private set { SetDescription(value); } }
        private List<string> imagesPaths;

        public Landmark(string aTitle, double lat, double lng, string aDescription, string aPath)
        {
            Title = aTitle;
            latitude = lat;
            longitude = lng;
            Description = aDescription;
            imagesPaths = new List<string>();
            AddImage(aPath);
        }

        private void SetTitle(string value)
        {
            if (String.IsNullOrWhiteSpace(value)) {
                throw new InvalidLandmarkException("Name can't be empty");
            }
            title = value;
        }

        private void SetDescription(string value)
        {
            if (value == null) {
                throw new InvalidLandmarkException("Description can't be null");
            }
            description = value;
        }

        private void AddImage(string aPath)
        {
            if (!File.Exists(aPath)) {
                throw new InvalidLandmarkException("Image doesn't exist");
            }
            imagesPaths.Add(aPath);
        }
    }
}
