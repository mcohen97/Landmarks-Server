using ObligatorioISP.BusinessLogic.Exceptions;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.BusinessLogic
{
    public class Tour
    {
        private int id;
        private string title;
        private ICollection<Landmark> landmarks;

        public int Id { get { return id; }set { SetId(value); } }

        public Tour(int anId, string aTitle, ICollection<Landmark> someLandmarks) {
            Id = anId;
        }

        private void SetId(int value)
        {
            if (value < 0) {
                throw new InvalidTourException("Id can't be negative");
            }
            id = value;
        }
    }
}
