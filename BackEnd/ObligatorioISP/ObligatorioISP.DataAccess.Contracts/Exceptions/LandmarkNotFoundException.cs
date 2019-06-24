using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts.Exceptions
{
    public class LandmarkNotFoundException : EntityNotFoundException
    {
        public LandmarkNotFoundException() : base("Landmark not found")
        {
        }
    }
}
