using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.DataAccess
{
    public interface ISqlContext
    {
        void ExcecuteCommand(string command);

        ICollection<Dictionary<string, object>> ExcecuteRead(string query);
    }
}
