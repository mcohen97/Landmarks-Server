using ObligatorioISP.BusinessLogic.Exceptions;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace ObligatorioISP.BusinessLogic
{
    public class Tour
    {
        private int id;
        private string title;
        private string imagePath;
        private ICollection<Landmark> landmarks;

        public int Id { get { return id; } private set { SetId(value); } }
        public string Title { get { return title; } private set { SetTitle(value); } }
        public ICollection<Landmark> Landmarks { get { return landmarks; }private set { SetLandmarks(value); } }
        public string ImagePath { get { return imagePath; } private set { SetImage(value); } }
        public TourCategory Category { get; private set; }

        public Tour(int anId, string aTitle, ICollection<Landmark> someLandmarks, string imagePath, TourCategory category) {
            Id = anId;
            Title = aTitle;
            Landmarks = someLandmarks;
            ImagePath = imagePath;
            Category = category;
        }

        private void SetId(int value)
        {
            if (value < 0) {
                throw new InvalidTourException("Id can't be negative");
            }
            id = value;
        }

        private void SetTitle(string value)
        {
            if (String.IsNullOrWhiteSpace(value)) {
                throw new InvalidTourException("Title can't be empty");
            }
            title = value;
        }

        private void SetLandmarks(ICollection<Landmark> value)
        {
            if (value == null || !value.Any()) {
                throw new InvalidTourException("Landmarks list can't be null or empty");
            }
            landmarks = value;
        }

        private void SetImage(string path)
        {
            if (!File.Exists(path))
            {
                throw new InvalidTourException("Image doesn't exist");
            }
            imagePath = path;
        }
    }
}
