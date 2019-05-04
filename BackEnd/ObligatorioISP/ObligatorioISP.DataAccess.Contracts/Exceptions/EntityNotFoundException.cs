using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess.Contracts.Exceptions
{
    public abstract class EntityNotFoundException:Exception
    {
        public EntityNotFoundException(string message) : base(message) {
        }
    }
}
