IF OBJECT_ID('dbo.DISTANCE', 'FN') IS NOT NULL 
  DROP FUNCTION dbo.DISTANCE;

GO
CREATE FUNCTION DISTANCE (@lat_1 FLOAT, @lng_1 FLOAT, @lat_2 FLOAT, @lng_2 FLOAT)
RETURNS FLOAT
AS
BEGIN
DECLARE @earthRadiusKm INT = 6371;

DECLARE @dLat FLOAT =RADIANS(@lat_2 - @lat_1); 
DECLARE @dLng FLOAT=RADIANS(@lng_2 - @lng_1);

DECLARE @lat_1_rad FLOAT = RADIANS(@lat_1);
DECLARE @lat_2_rad FLOAT = RADIANS(@lat_2);

DECLARE @a FLOAT = POWER(SIN(@lat_1_rad/2),2) + POWER(SIN(@lat_2_rad/2),2) * COS(@lat_1_rad) * COS(@lat_2_rad);
DECLARE @c FLOAT = 2 * ATN2(SQUARE(@a),SQUARE(1-@a));
RETURN @earthRadiusKm * @c;
END;
GO