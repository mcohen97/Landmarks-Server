using ObligatorioISP.BusinessLogic.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ObligatorioISP.BusinessLogic
{
    public class Tour
    {
        private int id;
        private string title;
        private ICollection<Landmark> landmarks;

        public int Id { get { return id; } private set { SetId(value); } }
        public string Title { get { return title; } private set { SetTitle(value); } }
        public ICollection<Landmark> Landmarks { get { return landmarks; }private set { SetLandmarks(value); } }

        public Tour(int anId, string aTitle, ICollection<Landmark> someLandmarks) {
            Id = anId;
            Title = aTitle;
            Landmarks = someLandmarks;
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
    }
}
